package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gear passives (CoreRpg 1.13.0, phase 2 — from the /workspace/wip draft; docs/ember-skills-passives.md), numbers in passives.yml:
 *  T1 blade  余烬引燃 — chance to ignite: fire + ember DoT (fraction of a full-charge hit per second), internal cooldown.
 *  T2 blade  炽愈     — heal on crit (CoreRpg crit or vanilla jump crit), internal cooldown.
 *  T3 blade  烬爆     — every Nth charged hit an ember burst hits mobs around the target, internal cooldown.
 *  余烬同袍 set — +X% damage while ≥ min_wearers set wearers (incl. self) are within radius (kill heal lives in SetService).
 * Only basic swings proc (skill / passive damage is flagged SkillService.internalDamage).
 */
public final class GearPassiveService implements Listener {

    private final CoreRpgPlugin plugin;
    private final NiBridge ni;

    private boolean enabled = true, debug = false;
    public boolean isDebug() { return debug; }
    public void log(String m) { if (debug) plugin.getLogger().info("passive " + m); }
    private String t1Id = "gear_ember_t1_blade", t2Id = "gear_ember_t2_blade", t3Id = "gear_ember_t3_blade";
    private double igniteChance = 0.15, igniteDotMult = 0.12; private int igniteSeconds = 3; private long igniteIcd = 6000;
    private double critHealFlat = 2.0, critHealPctMax = 0.04; private long critHealIcd = 3000;
    private int burstEvery = 5; private double burstMult = 0.6, burstRadius = 3.0; private int burstMaxTargets = 5; private long burstIcd = 2500;
    private double minCharge = 0.7;
    private boolean setEnabled = true; private double setDamagePct = 0.05, setRadius = 24; private int setMinWearers = 2;

    private final Map<UUID, Long> icd = new HashMap<UUID, Long>();
    private final Map<UUID, Integer> hitCount = new HashMap<UUID, Integer>();
    private final Map<UUID, long[]> setCache = new HashMap<UUID, long[]>(); // [expiry, active?1:0]

    public GearPassiveService(CoreRpgPlugin plugin, NiBridge ni) {
        this.plugin = plugin;
        this.ni = ni;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void reload() {
        File f = new File(plugin.getDataFolder(), "passives.yml");
        if (!f.exists()) plugin.saveResource("passives.yml", false);
        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        InputStream in = plugin.getResource("passives.yml");
        if (in != null) c.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
        enabled = c.getBoolean("enabled", true);
        minCharge = c.getDouble("min_charge", 0.7);
        debug = c.getBoolean("debug", false);
        ConfigurationSection s = c.getConfigurationSection("t1_ignite");
        if (s != null) {
            t1Id = s.getString("ni_id", t1Id); igniteChance = s.getDouble("chance", 0.15);
            igniteSeconds = s.getInt("seconds", 3); igniteDotMult = s.getDouble("dot_mult_per_second", 0.12);
            igniteIcd = (long) (1000 * s.getDouble("icd_seconds", 6));
        }
        s = c.getConfigurationSection("t2_crit_heal");
        if (s != null) {
            t2Id = s.getString("ni_id", t2Id); critHealFlat = s.getDouble("heal", 2.0);
            critHealPctMax = s.getDouble("heal_pct_max", 0.04); critHealIcd = (long) (1000 * s.getDouble("icd_seconds", 3));
        }
        s = c.getConfigurationSection("t3_burst");
        if (s != null) {
            t3Id = s.getString("ni_id", t3Id); burstEvery = Math.max(2, s.getInt("every_hits", 5));
            burstMult = Math.min(2.5, s.getDouble("damage_mult", 0.6)); burstRadius = s.getDouble("radius", 3.0);
            burstMaxTargets = s.getInt("max_targets", 5); burstIcd = (long) (1000 * s.getDouble("icd_seconds", 2.5));
        }
        s = c.getConfigurationSection("set_comrade");
        if (s != null) {
            setEnabled = s.getBoolean("enabled", true); setDamagePct = Math.min(0.25, s.getDouble("damage_pct", 0.05));
            setRadius = s.getDouble("radius", 24); setMinWearers = Math.max(1, s.getInt("min_wearers", 2));
        }
        icd.clear(); hitCount.clear(); setCache.clear();
    }

    private boolean ready(UUID u, String key, long ms) {
        UUID k = new UUID(u.getMostSignificantBits(), u.getLeastSignificantBits() ^ key.hashCode());
        long now = System.currentTimeMillis();
        Long t = icd.get(k);
        if (t != null && now < t.longValue()) return false;
        icd.put(k, Long.valueOf(now + ms));
        return true;
    }

    private String handId(Player p) {
        ItemStack h = p.getInventory().getItemInMainHand();
        if (h == null || h.getType() == Material.AIR) return null;
        return ni.getNiId(h);
    }

    private boolean setActive(Player p) {
        long now = System.currentTimeMillis();
        long[] v = setCache.get(p.getUniqueId());
        if (v != null && now < v[0]) return v[1] > 0;
        SetService ss = plugin.getSetService();
        boolean a = ss != null && ss.isSetActive(p);
        setCache.put(p.getUniqueId(), new long[] { now + 2000, a ? 1 : 0 });
        return a;
    }

    /** Wearers of the comrade set within radius (incl. p), for /corerpg set and the damage bonus. */
    public int nearbyWearers(Player p) {
        if (!setActive(p)) return 0;
        int n = 1;
        double r2 = setRadius * setRadius;
        for (Player o : p.getWorld().getPlayers()) {
            if (o.equals(p) || o.isDead()) continue;
            if (o.getLocation().distanceSquared(p.getLocation()) <= r2 && setActive(o)) n++;
        }
        return n;
    }

    public String describeSet(Player p) {
        int n = nearbyWearers(p);
        return ChatColor.GRAY + "  同袍共鸣：" + setRadius + " 格内同袍 " + n + "/" + setMinWearers
                + (n >= setMinWearers ? ChatColor.GREEN + " · 伤害 +" + Math.round(setDamagePct * 100) + "%" : ChatColor.DARK_GRAY + " · 未共鸣");
    }

    /** Set bonus multiplies the finished hit (HIGH, same stage as 灰印 / calamity scaling). */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSetBonus(EntityDamageByEntityEvent e) {
        if (!enabled || !setEnabled || !(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof LivingEntity) || e.getEntity() instanceof Player || e.getEntity() instanceof ArmorStand) return;
        Player p = (Player) e.getDamager();
        if (nearbyWearers(p) >= setMinWearers) {
            e.setDamage(e.getDamage() * (1.0 + setDamagePct));
            if (debug && ThreadLocalRandom.current().nextDouble() < 0.05) log("set " + p.getName() + " +" + Math.round(setDamagePct * 100) + "% (sampled 5%)");
        }
    }

    /** Blade procs after the hit is final (MONITOR, read-only on the event; extra effects are separate). */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!enabled || SkillService.internalDamage) return;
        if (!(e.getDamager() instanceof Player) || e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(e.getEntity() instanceof LivingEntity) || e.getEntity() instanceof Player || e.getEntity() instanceof ArmorStand) return;
        final Player p = (Player) e.getDamager();
        final LivingEntity victim = (LivingEntity) e.getEntity();
        String id = handId(p);
        if (id == null) return;
        StatService st = plugin.getStatService();
        if (st == null) return;
        double charge = st.lastCharge(p.getUniqueId());
        UUID u = p.getUniqueId();
        if (id.equals(t1Id)) {
            if (charge >= minCharge && ThreadLocalRandom.current().nextDouble() < igniteChance && ready(u, "ignite", igniteIcd)) {
                final double tick = igniteDotMult * st.fullHitDamage(p);
                victim.setFireTicks(Math.max(victim.getFireTicks(), igniteSeconds * 20));
                log("ignite " + p.getName() + " tick=" + String.format("%.1f", tick));
                victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0, 1, 0), 14, 0.3, 0.5, 0.3, 0.02);
                for (int i = 1; i <= igniteSeconds; i++) {
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override public void run() {
                            if (p.isOnline() && !victim.isDead() && victim.isValid()) {
                                SkillService.dealInternal(p, victim, tick);
                                victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0, 1, 0), 6, 0.3, 0.5, 0.3, 0.01);
                            }
                        }
                    }, 20L * i);
                }
            }
        } else if (id.equals(t2Id)) {
            if (st.lastCrit(u) && ready(u, "critheal", critHealIcd)) {
                double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double heal = Math.min(critHealFlat, max * critHealPctMax <= 0 ? critHealFlat : Math.max(critHealFlat * 0.5, max * critHealPctMax));
                p.setHealth(Math.min(max, p.getHealth() + heal));
                log("critheal " + p.getName() + " +" + String.format("%.1f", heal));
                p.getWorld().spawnParticle(Particle.HEART, p.getLocation().add(0, 2, 0), 3, 0.3, 0.2, 0.3, 0);
            }
        } else if (id.equals(t3Id)) {
            if (charge < minCharge) return;
            int n = (hitCount.containsKey(u) ? hitCount.get(u).intValue() : 0) + 1;
            if (n >= burstEvery && ready(u, "burst", burstIcd)) {
                n = 0;
                final double dmg = burstMult * st.fullHitDamage(p);
                log("burst " + p.getName() + " dmg=" + String.format("%.1f", dmg));
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override public void run() {
                        if (!p.isOnline() || !victim.isValid()) return;
                        int hit = 0;
                        for (Entity en : victim.getNearbyEntities(burstRadius, burstRadius, burstRadius)) {
                            if (hit >= burstMaxTargets - 1) break;
                            if (!(en instanceof LivingEntity) || en instanceof Player || en instanceof ArmorStand || en.isDead()) continue;
                            SkillService.dealInternal(p, (LivingEntity) en, dmg); hit++;
                        }
                        if (!victim.isDead()) SkillService.dealInternal(p, victim, dmg);
                        victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0, 1, 0), 12, burstRadius / 2, 0.5, burstRadius / 2, 0);
                        victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.4f);
                    }
                });
            }
            hitCount.put(u, Integer.valueOf(Math.min(n, burstEvery)));
        }
    }

    public void onQuit(UUID u) { hitCount.remove(u); setCache.remove(u); }
}
