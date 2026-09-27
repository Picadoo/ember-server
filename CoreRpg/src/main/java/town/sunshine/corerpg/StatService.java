package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gear stat layer (2026-09-27). AttributePlus is parked, so CoreRpg applies the NI lore stats itself:
 * main-hand weapon lore (物理伤害 +N) + best accessory in inventory/offhand (生命力 / 物理防御)
 * + enhance level × enhance.yml stat_per_level + socket gems + reforge affixes + unlocked talent stats.
 * Scales/curve in config.yml {@code stats:}.
 */
public final class StatService implements Listener {

    private static final Pattern P_DMG = Pattern.compile("物理伤害\\s*[:：]?\\s*\\+\\s*(\\d+(?:\\.\\d+)?)");
    private static final Pattern P_HP = Pattern.compile("生命力\\s*[:：]?\\s*\\+\\s*(\\d+(?:\\.\\d+)?)");
    private static final Pattern P_DEF = Pattern.compile("物理防御\\s*[:：]?\\s*\\+\\s*(\\d+(?:\\.\\d+)?)");

    private final CoreRpgPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;
    private final Map<UUID, Map<String, Double>> cache = new HashMap<UUID, Map<String, Double>>();

    private boolean enabled = true;
    private double damageScale = 1.0;
    private double healthScale = 1.0;
    private double defenseK = 20.0;
    private double critMultiplier = 1.5;
    private double lifeStealCap = 0.05;
    /** 1.13.0 phase 2: caps for covenant / gem pseudo-stats that now have an attribute layer */
    private double moveSpeedCap = 0.08, attackSpeedCap = 0.10, skillLifeStealCap = 0.15, skillHealPerCastPct = 0.08;
    private static final UUID AS_MOD = UUID.fromString("6e6d6265-722d-4153-2d6d-6f6400000001");
    private double heartsDisplayCap = 40.0;
    private boolean stripWitherOnHit = true;
    /** config stats.apply_covenant_stats (default false): covenant pseudo-stats are coded but parked until the phase-2 rebalance */
    private boolean applyCovenantStats = false;
    private final List<String> weaponIds = new ArrayList<String>();
    private final List<String> accessoryIds = new ArrayList<String>();
    private final Map<String, Map<String, Double>> perLevel = new HashMap<String, Map<String, Double>>();
    private final Map<String, Map<String, Double>> gems = new HashMap<String, Map<String, Double>>();

    public StatService(CoreRpgPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void reload() {
        FileConfiguration c = plugin.getConfig();
        enabled = c.getBoolean("stats.enabled", true);
        damageScale = c.getDouble("stats.damage_scale", 1.0);
        healthScale = c.getDouble("stats.health_scale", 1.0);
        defenseK = Math.max(1.0, c.getDouble("stats.defense_k", 20.0));
        critMultiplier = c.getDouble("stats.crit_multiplier", 1.5);
        heartsDisplayCap = c.getDouble("stats.hearts_display_cap", 40.0);
        stripWitherOnHit = c.getBoolean("stats.strip_wither_on_hit", true);
        applyCovenantStats = c.getBoolean("stats.apply_covenant_stats", true);
        attackSpeedCap = c.getDouble("stats.caps.attack_speed_pct", 0.10);
        skillLifeStealCap = c.getDouble("stats.caps.skill_life_steal_pct", 0.15);
        skillHealPerCastPct = c.getDouble("stats.caps.skill_heal_per_cast_pct", 0.08);
        weaponIds.clear();
        accessoryIds.clear();
        List<String> w = c.getStringList("stats.weapons");
        List<String> a = c.getStringList("stats.accessories");
        if (w == null || w.isEmpty()) {
            w = java.util.Arrays.asList("gear_ember_blade", "gear_ember_t1_blade", "gear_ember_t2_blade", "gear_ember_t3_blade");
        }
        if (a == null || a.isEmpty()) {
            a = java.util.Arrays.asList("gear_ember_charm", "gear_ember_t1_talisman", "gear_ember_t2_talisman", "gear_ember_t3_talisman");
        }
        weaponIds.addAll(w);
        accessoryIds.addAll(a);
        perLevel.clear();
        gems.clear();
        File ef = new File(plugin.getDataFolder(), "enhance.yml");
        if (ef.exists()) {
            YamlConfiguration e = YamlConfiguration.loadConfiguration(ef);
            readMapOfMaps(e.getConfigurationSection("stat_per_level"), perLevel);
            readMapOfMaps(e.getConfigurationSection("gems"), gems);
            lifeStealCap = e.getDouble("caps.life_steal_pct", 0.05);
            moveSpeedCap = e.getDouble("caps.move_speed_pct", 0.08);
        }
        cache.clear();
    }

    private static void readMapOfMaps(ConfigurationSection s, Map<String, Map<String, Double>> out) {
        if (s == null) return;
        for (String k : s.getKeys(false)) {
            ConfigurationSection g = s.getConfigurationSection(k);
            if (g == null) continue;
            Map<String, Double> m = new HashMap<String, Double>();
            for (String st : g.getKeys(false)) m.put(st, Double.valueOf(g.getDouble(st)));
            out.put(k, m);
        }
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) refresh(p);
            }
        }, 40L, 20L);
    }

    // ---------------------------------------------------------------- compute

    private static void add(Map<String, Double> m, String k, double v) {
        if (v == 0) return;
        Double o = m.get(k);
        m.put(k, Double.valueOf((o == null ? 0 : o.doubleValue()) + v));
    }

    private static double get(Map<String, Double> m, String k) {
        Double v = m.get(k);
        return v == null ? 0 : v.doubleValue();
    }

    /** Stats of one ember gear item (lore base + enhance + sockets + affixes). */
    private Map<String, Double> itemStats(ItemStack stack, String niId) {
        Map<String, Double> m = new HashMap<String, Double>();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null && meta.hasLore()) {
            for (String line : meta.getLore()) {
                if (line == null) continue;
                String plain = ChatColor.stripColor(line);
                if (plain == null || plain.startsWith("次要") || plain.startsWith("#")) continue;
                Matcher x = P_DMG.matcher(plain);
                if (x.find()) add(m, "phys_damage", Double.parseDouble(x.group(1)));
                x = P_HP.matcher(plain);
                if (x.find()) add(m, "max_health", Double.parseDouble(x.group(1)));
                x = P_DEF.matcher(plain);
                if (x.find()) add(m, "phys_defense", Double.parseDouble(x.group(1)));
            }
        }
        int lv = GearLore.readEnhance(stack);
        Map<String, Double> pl = perLevel.get(niId);
        if (pl != null && lv > 0) {
            for (Map.Entry<String, Double> e : pl.entrySet()) add(m, e.getKey(), e.getValue().doubleValue() * lv);
        }
        for (String g : GearLore.readSockets(stack, 6)) {
            if (g == null || g.isEmpty()) continue;
            Map<String, Double> gs = gems.get(g);
            if (gs != null) for (Map.Entry<String, Double> e : gs.entrySet()) add(m, e.getKey(), e.getValue().doubleValue());
        }
        for (GearLore.Affix af : GearLore.readAffixes(stack)) {
            if ("crit_chance".equals(af.id)) add(m, "crit_chance_pct", af.value / 100.0);
            else if ("attack_speed".equals(af.id)) add(m, "attack_speed_pct", af.value / 100.0);
            else add(m, af.id, af.value);
        }
        return m;
    }

    public Map<String, Double> compute(Player p) {
        Map<String, Double> total = new LinkedHashMap<String, Double>();
        PlayerInventory inv = p.getInventory();
        ItemStack hand = inv.getItemInMainHand();
        String hid = hand == null || hand.getType() == Material.AIR ? null : ni.getNiId(hand);
        if (hid != null && weaponIds.contains(hid)) {
            for (Map.Entry<String, Double> e : itemStats(hand, hid).entrySet()) add(total, e.getKey(), e.getValue());
        }
        // best single accessory anywhere in the inventory (incl. offhand)
        Map<String, Double> best = null;
        double bestScore = -1;
        List<ItemStack> all = new ArrayList<ItemStack>();
        for (ItemStack s : inv.getStorageContents()) all.add(s);
        all.add(inv.getItemInOffHand());
        for (ItemStack s : all) {
            if (s == null || s.getType() == Material.AIR) continue;
            String id = ni.getNiId(s);
            if (id == null || !accessoryIds.contains(id)) continue;
            Map<String, Double> st = itemStats(s, id);
            double score = get(st, "max_health") + get(st, "phys_defense") * 3;
            if (score > bestScore) { bestScore = score; best = st; }
        }
        Map<String, Double> talent = new HashMap<String, Double>();
        PlayerData d = dataStore.peek(p.getUniqueId());
        TalentService ts = plugin.getTalentService();
        if (d != null && ts != null) {
            for (String node : new ArrayList<String>(d.getTalentNodes())) {
                TalentService.NodeDef nd = ts.getNode(node);
                if (nd == null || nd.stats == null) continue;
                for (Map.Entry<String, Double> e : nd.stats.entrySet()) add(talent, e.getKey(), e.getValue().doubleValue());
            }
        }
        if (best != null) {
            double bonus = 1.0 + get(talent, "charm_stat_bonus_pct");
            for (Map.Entry<String, Double> e : best.entrySet()) add(total, e.getKey(), e.getValue() * bonus);
        }
        for (Map.Entry<String, Double> e : talent.entrySet()) {
            if (!"charm_stat_bonus_pct".equals(e.getKey())) add(total, e.getKey(), e.getValue());
        }
        // covenant pseudo-stats (covenant.yml covenants.<id>.stats), only with stats.apply_covenant_stats: true (blaze 1% life steal
        // alone lifted a solo weekly from ~45% to 93% end HP in the 1.10.0 skill test → parked for the phase-2 rebalance)
        CovenantService cs = plugin.getCovenantService();
        if (applyCovenantStats && d != null && cs != null && d.hasCovenant()) {
            CovenantService.CovenantDef cd = cs.get(d.getCovenant());
            if (cd != null) {
                for (Map.Entry<String, Double> e : cd.stats.entrySet()) {
                    if ("charm_stat_bonus_pct".equals(e.getKey())) continue; // folded into the accessory above
                    add(total, e.getKey(), e.getValue().doubleValue());
                }
                if (best != null && cd.stats.containsKey("charm_stat_bonus_pct")) {
                    double b = cd.stats.get("charm_stat_bonus_pct").doubleValue();
                    add(total, "max_health", get(best, "max_health") * b);
                    add(total, "phys_defense", get(best, "phys_defense") * b);
                }
            }
        }
        if (get(total, "crit_chance_pct") > 0.35) total.put("crit_chance_pct", Double.valueOf(0.35));
        if (get(total, "damage_taken_pct") < -0.25) total.put("damage_taken_pct", Double.valueOf(-0.25));
        return total;
    }

    public double stat(Player p, String key) { return get(stats(p), key); }

    /**
     * 1.13.0: skill life steal (blaze covenant) — heal a share of the damage one skill cast dealt, capped per cast
     * (skill_heal_per_cast_pct × max HP). Basic-hit life steal stays gem-only (life_steal_pct, cap 5%).
     */
    public double skillHeal(Player p, double dealt) {
        if (!enabled || p == null || dealt <= 0) return 0;
        double pct = Math.min(skillLifeStealCap, get(stats(p), "skill_life_steal_pct"));
        if (pct <= 0) return 0;
        double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double heal = Math.min(dealt * pct, max * skillHealPerCastPct);
        if (heal <= 0 || p.isDead()) return 0;
        p.setHealth(Math.min(max, p.getHealth() + heal));
        GearPassiveService gp = plugin.getGearPassiveService();
        if (gp != null) gp.log("skillheal " + p.getName() + " dealt=" + String.format("%.1f", dealt) + " +" + String.format("%.1f", heal));
        return heal;
    }

    /**
     * Full-charge basic melee hit right now: vanilla attack attribute (weapon) + Sharpness (0.5·lvl + 0.5) + gear phys_damage.
     * No crit. Skills / passives scale off this (docs/ember-skills-passives.md).
     */
    public double fullHitDamage(Player p) {
        double base = 1.0;
        AttributeInstance ai = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (ai != null) base = ai.getValue();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand != null && hand.getType() != Material.AIR) {
            int sh = hand.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DAMAGE_ALL);
            if (sh > 0) base += 0.5 * sh + 0.5;
        }
        return base + (enabled ? get(stats(p), "phys_damage") * damageScale : 0);
    }

    /** Per-player info about the last basic hit (for gear passives at MONITOR): charge 0..1 and whether it crit. */
    private final Map<UUID, double[]> lastSwing = new HashMap<UUID, double[]>();
    public double lastCharge(UUID u) { double[] v = lastSwing.get(u); return v == null ? 0 : v[0]; }
    public boolean lastCrit(UUID u) { double[] v = lastSwing.get(u); return v != null && v[1] > 0; }

    private Map<String, Double> stats(Player p) {
        Map<String, Double> m = cache.get(p.getUniqueId());
        if (m == null) { m = compute(p); cache.put(p.getUniqueId(), m); }
        return m;
    }

    public void refresh(Player p) {
        if (p == null || !p.isOnline()) return;
        Map<String, Double> m = compute(p);
        cache.put(p.getUniqueId(), m);
        AttributeInstance ai = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (ai == null) return;
        double want = enabled ? 20.0 + Math.floor(get(m, "max_health") * healthScale) : 20.0;
        if (Math.abs(ai.getBaseValue() - want) > 0.01) {
            ai.setBaseValue(want);
            if (p.getHealth() > want) p.setHealth(want);
        }
        // 1.13.0: move / attack speed pseudo-stats (gale gem, 灰行 covenant) — capped, vanilla attribute layer
        float ws = (float) (0.2 * (1.0 + (enabled ? Math.min(moveSpeedCap, Math.max(0, get(m, "move_speed_pct"))) : 0)));
        if (Math.abs(p.getWalkSpeed() - ws) > 0.0005) p.setWalkSpeed(ws);
        AttributeInstance as = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (as != null) {
            double asp = enabled ? Math.min(attackSpeedCap, Math.max(0, get(m, "attack_speed_pct"))) : 0;
            org.bukkit.attribute.AttributeModifier old = null;
            for (org.bukkit.attribute.AttributeModifier am : as.getModifiers()) if (AS_MOD.equals(am.getUniqueId())) old = am;
            if (old == null || Math.abs(old.getAmount() - asp) > 1e-6) {
                if (old != null) as.removeModifier(old);
                if (asp > 0) as.addModifier(new org.bukkit.attribute.AttributeModifier(AS_MOD, "ember_attack_speed", asp,
                        org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }
        }
        double scale = Math.min(heartsDisplayCap, Math.max(20.0, want));
        if (!p.isHealthScaled() || Math.abs(p.getHealthScale() - scale) > 0.01) {
            p.setHealthScale(scale);
            p.setHealthScaled(true);
        }
    }

    private void refreshLater(final Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override public void run() { refresh(p); }
        }, 1L);
    }

    // ---------------------------------------------------------------- events

    @EventHandler public void onJoin(PlayerJoinEvent e) { refreshLater(e.getPlayer()); }
    @EventHandler public void onRespawn(PlayerRespawnEvent e) { refreshLater(e.getPlayer()); }
    @EventHandler public void onHeld(PlayerItemHeldEvent e) { refreshLater(e.getPlayer()); }

    private final Map<UUID, Long> lastHit = new HashMap<UUID, Long>();
    private final double swingMs = 625.0;

    /** Player → mob flat bonus runs first (LOWEST) so MythicMobs DamageModifiers scale the whole hit. */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!enabled) return;
        Entity victim = e.getEntity();
        Entity damager = e.getDamager();
        if (damager instanceof Player && victim instanceof LivingEntity && !(victim instanceof Player)
                && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (SkillService.internalDamage) return; // skill / passive damage already scales off fullHitDamage
            Player p = (Player) damager;
            Map<String, Double> m = stats(p);
            double flat = get(m, "phys_damage") * damageScale;
            if (flat <= 0 && get(m, "crit_chance_pct") <= 0) { lastSwing.put(p.getUniqueId(), new double[] { 1.0, 0 }); return; }
            // attack-charge approximation (1.12 API has no getAttackCooldown): time since this player's last hit,
            // vanilla curve 0.2 + 0.8·t² over the sword cooldown → spam-clicking gets ~20% of the bonus
            long now = System.currentTimeMillis();
            Long last = lastHit.put(p.getUniqueId(), Long.valueOf(now));
            double swing = swingMs / (1.0 + Math.min(attackSpeedCap, Math.max(0, get(m, "attack_speed_pct"))));
            double t = last == null ? 1.0 : Math.min(1.0, (now - last.longValue()) / swing);
            double charge = 0.2 + 0.8 * t * t;
            double dmg = e.getDamage() + flat * charge;
            boolean crit = false;
            if (charge > 0.9 && ThreadLocalRandom.current().nextDouble() < get(m, "crit_chance_pct")) {
                dmg *= critMultiplier + get(m, "crit_damage_pct");
                crit = true;
            }
            // vanilla jump crit (falling, airborne, not sprinting) also counts as a crit for passives
            if (!crit && charge > 0.9 && p.getFallDistance() > 0 && !p.isOnGround() && !p.isSprinting()
                    && !p.isInsideVehicle() && !p.hasPotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS)) crit = true;
            lastSwing.put(p.getUniqueId(), new double[] { charge, crit ? 1 : 0 });
            e.setDamage(dmg);
            double ls = Math.min(lifeStealCap, get(m, "life_steal_pct"));
            if (ls > 0) {
                double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                p.setHealth(Math.min(max, p.getHealth() + dmg * ls));
            }
        }
    }

    /** Mob → player defense runs late (HIGH) so it reduces the final hit. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDefend(EntityDamageByEntityEvent e) {
        if (!enabled) return;
        Entity victim = e.getEntity();
        Entity damager = e.getDamager();
        if (victim instanceof Player) {
            Entity src = damager;
            if (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Entity) {
                src = (Entity) ((Projectile) damager).getShooter();
            }
            if (src instanceof Player) return; // PvP untouched (arena balance)
            Map<String, Double> m = stats((Player) victim);
            double def = get(m, "phys_defense");
            double mult = (1.0 - def / (def + defenseK)) * (1.0 + get(m, "damage_taken_pct"));
            if (mult < 0.999) e.setDamage(e.getDamage() * Math.max(0.2, mult));
            if (stripWitherOnHit && src instanceof org.bukkit.entity.WitherSkeleton) {
                // vanilla wither-skeleton melee adds 10 s Wither I that ticks through defense; for Ember bosses
                // (abyss watcher, calamity, raid) it dominated solo fights → strip it next tick (config stats.strip_wither_on_hit)
                final Player pv = (Player) victim;
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override public void run() {
                        for (org.bukkit.potion.PotionEffect pe : pv.getActivePotionEffects()) {
                            if (pe.getType().equals(org.bukkit.potion.PotionEffectType.WITHER)
                                    && pe.getAmplifier() == 0 && pe.getDuration() > 150) {
                                pv.removePotionEffect(org.bukkit.potion.PotionEffectType.WITHER);
                            }
                        }
                    }
                }, 1L);
            }
        }
    }

    // ---------------------------------------------------------------- command

    public boolean cmd(org.bukkit.command.CommandSender sender, String[] args) {
        Player target = null;
        if (args.length >= 2 && sender.hasPermission("corerpg.admin")) target = Bukkit.getPlayerExact(args[1]);
        else if (sender instanceof Player) target = (Player) sender;
        if (target == null) { sender.sendMessage(ChatColor.RED + "[属性] 找不到玩家"); return true; }
        Map<String, Double> m = compute(target);
        double def = get(m, "phys_defense");
        sender.sendMessage(ChatColor.GOLD + "[属性] " + target.getName()
                + ChatColor.GRAY + " 攻击 +" + fmt(get(m, "phys_damage") * damageScale)
                + " · 生命 " + fmt(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                + " · 减伤 " + fmt(100.0 * def / (def + defenseK)) + "%"
                + " · 暴击 " + fmt(100.0 * get(m, "crit_chance_pct")) + "%");
        PlayerData pd = dataStore.get(target.getUniqueId());
        CovenantService cs = plugin.getCovenantService();
        if (pd != null && cs != null) {
            if (!pd.hasCovenant()) sender.sendMessage(ChatColor.GRAY + "  誓约：未选择（/corerpg covenant）");
            else {
                CovenantService.CovenantDef cd = cs.get(pd.getCovenant());
                sender.sendMessage(ChatColor.GRAY + "  誓约：" + cs.displayColored(pd.getCovenant()) + ChatColor.GRAY
                        + (cd == null ? "" : " " + cs.formatStats(cd))
                        + (applyCovenantStats ? ChatColor.GREEN + "（已计入）" : ChatColor.DARK_GRAY + "（誓约属性待第 2 阶段平衡后生效，当前仅技能）"));
            }
        }
        sender.sendMessage(ChatColor.DARK_GRAY + "  raw " + m);
        return true;
    }

    private static String fmt(double v) {
        return String.format("%.1f", v);
    }
}
