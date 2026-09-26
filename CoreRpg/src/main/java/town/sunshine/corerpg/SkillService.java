package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple covenant active skills — DESIGN-ember-simple-skills.md.
 * CD in-memory only; flat damage; ignore enhance multiplier.
 */
public final class SkillService implements Listener {

    private static final String PREFIX = ChatColor.LIGHT_PURPLE + "[技能] " + ChatColor.RESET;

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private boolean ignoreEnhanceMultiplier = true;
    private double damageCapGlobal = 12.0;

    private final Map<String, SkillDef> skills = new LinkedHashMap<String, SkillDef>();
    /** covenant id -> skill id */
    private final Map<String, String> covenantToSkill = new HashMap<String, String>();

    /** UUID -> skillId -> expireMillis */
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<UUID, Map<String, Long>>();

    /** attacker UUID -> active ash mark */
    private final Map<UUID, AshMark> ashMarks = new HashMap<UUID, AshMark>();

    public static final class SkillDef {
        public final String id;
        public final String covenant;
        public final String display;
        public final int cooldownSeconds;
        public final double range;
        public final double arcDegrees;
        public final double damage;
        public final double damageCap;
        public final int talentBonusMax;
        public final int slowAmplifier;
        public final int slowTicks;
        public final double markBonusDamage;
        public final int markDurationTicks;
        public final double radius;
        public final int resistanceAmplifier;
        public final int resistanceTicks;
        public final String particles;
        public final String sound;

        SkillDef(String id, String covenant, String display, int cooldownSeconds,
                 double range, double arcDegrees, double damage, double damageCap,
                 int talentBonusMax, int slowAmplifier, int slowTicks,
                 double markBonusDamage, int markDurationTicks, double radius,
                 int resistanceAmplifier, int resistanceTicks,
                 String particles, String sound) {
            this.id = id;
            this.covenant = covenant;
            this.display = display;
            this.cooldownSeconds = cooldownSeconds;
            this.range = range;
            this.arcDegrees = arcDegrees;
            this.damage = damage;
            this.damageCap = damageCap;
            this.talentBonusMax = talentBonusMax;
            this.slowAmplifier = slowAmplifier;
            this.slowTicks = slowTicks;
            this.markBonusDamage = markBonusDamage;
            this.markDurationTicks = markDurationTicks;
            this.radius = radius;
            this.resistanceAmplifier = resistanceAmplifier;
            this.resistanceTicks = resistanceTicks;
            this.particles = particles;
            this.sound = sound;
        }
    }

    private static final class AshMark {
        final UUID victim;
        final long expireMillis;
        final double bonusDamage;
        final double damageCap;
        AshMark(UUID victim, long expireMillis, double bonusDamage, double damageCap) {
            this.victim = victim;
            this.expireMillis = expireMillis;
            this.bonusDamage = bonusDamage;
            this.damageCap = damageCap;
        }
    }

    public SkillService(CoreRpgPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "skills.yml");
        if (!file.exists()) {
            plugin.saveResource("skills.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("skills.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        loadFrom(cfg);
    }

    private void loadFrom(FileConfiguration cfg) {
        skills.clear();
        covenantToSkill.clear();
        enabled = cfg.getBoolean("enabled", true);
        ignoreEnhanceMultiplier = cfg.getBoolean("ignore_enhance_multiplier", true);
        damageCapGlobal = cfg.getDouble("damage_cap_global", 12.0);
        ConfigurationSection root = cfg.getConfigurationSection("skills");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(id);
            if (sec == null) continue;
            String covenant = sec.getString("covenant", "");
            SkillDef def = new SkillDef(
                    id,
                    covenant == null ? "" : covenant.toLowerCase(),
                    color(sec.getString("display", id)),
                    Math.max(0, sec.getInt("cooldown_seconds", 8)),
                    sec.getDouble("range", 3.0),
                    sec.getDouble("arc_degrees", 90.0),
                    sec.getDouble("damage", 0),
                    sec.getDouble("damage_cap", 10.0),
                    Math.max(0, sec.getInt("talent_bonus_max", 0)),
                    sec.getInt("slow_amplifier", 0),
                    sec.getInt("slow_ticks", 60),
                    sec.getDouble("mark_bonus_damage", 3),
                    sec.getInt("mark_duration_ticks", 80),
                    sec.getDouble("radius", 4.0),
                    sec.getInt("resistance_amplifier", 0),
                    sec.getInt("resistance_ticks", 80),
                    sec.getString("particles", null),
                    sec.getString("sound", null));
            skills.put(id, def);
            if (def.covenant != null && !def.covenant.isEmpty()) {
                covenantToSkill.put(def.covenant, id);
            }
        }
    }

    private static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s.replace('§', '&'));
    }

    public boolean isEnabled() { return enabled; }

    public SkillDef skillForCovenant(String covenant) {
        if (covenant == null || covenant.isEmpty() || "none".equalsIgnoreCase(covenant)) return null;
        String id = covenantToSkill.get(covenant.toLowerCase());
        return id == null ? null : skills.get(id);
    }

    public SkillDef getSkill(String id) {
        return id == null ? null : skills.get(id);
    }

    public void cmdRoot(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only");
            return;
        }
        Player player = (Player) sender;
        if (!enabled) {
            player.sendMessage(PREFIX + ChatColor.RED + "功能未启用");
            return;
        }
        if (args.length >= 2 && "info".equalsIgnoreCase(args[1])) {
            cmdInfo(player);
            return;
        }
        cast(player);
    }

    public void cmdInfo(Player player) {
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasCovenant()) {
            player.sendMessage(PREFIX + ChatColor.RED + "请先选定誓约 /corerpg covenant set <blaze|ash|warden>");
            return;
        }
        SkillDef def = skillForCovenant(data.getCovenant());
        if (def == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "当前誓约无对应技能");
            return;
        }
        long remainMs = remainingCooldownMs(player.getUniqueId(), def.id);
        String cdRemain;
        if (remainMs > 0) {
            cdRemain = ChatColor.RED + "冷却中 " + String.format("%.0f", Math.ceil(remainMs / 1000.0)) + "s";
        } else {
            cdRemain = ChatColor.GREEN + "就绪";
        }
        int approxDmg = (int) Math.round(computeFlatDamage(player, def, def.damage));
        player.sendMessage(PREFIX + def.display + ChatColor.GRAY + " · " + cdRemain);
        player.sendMessage(ChatColor.GRAY + "  冷却 " + def.cooldownSeconds + "s"
                + " · 伤害约 " + approxDmg
                + (ignoreEnhanceMultiplier ? " · 不吃强化加成" : ""));
        if ("ember_blaze_slash".equals(def.id)) {
            player.sendMessage(ChatColor.GRAY + "  前方挥砍，命中附近敌人");
        } else if ("ember_ash_familiar".equals(def.id) || def.id.contains("ash")) {
            double mark = Math.min(def.damageCap, Math.min(damageCapGlobal, def.markBonusDamage));
            player.sendMessage(ChatColor.GRAY + "  给敌人上减速印，下次普攻约 +" + ((int) Math.round(mark)));
        } else if ("ember_warden_taunt".equals(def.id)) {
            player.sendMessage(ChatColor.GRAY + "  自身减伤，周围敌人减速");
        }
        player.sendMessage(ChatColor.DARK_GRAY + "  /corerpg skill 释放");
    }

    public void cast(Player player) {
        if (!enabled) {
            player.sendMessage(PREFIX + ChatColor.RED + "功能未启用");
            return;
        }
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!data.hasCovenant()) {
            player.sendMessage(PREFIX + ChatColor.RED + "请先选定誓约 /corerpg covenant set <blaze|ash|warden>");
            return;
        }
        SkillDef def = skillForCovenant(data.getCovenant());
        if (def == null) {
            player.sendMessage(PREFIX + ChatColor.RED + "当前誓约无对应技能");
            return;
        }
        long remainMs = remainingCooldownMs(player.getUniqueId(), def.id);
        if (remainMs > 0) {
            int secs = (int) Math.ceil(remainMs / 1000.0);
            player.sendMessage(PREFIX + ChatColor.RED + "冷却中，剩余 " + secs + "s");
            return;
        }

        boolean ok;
        if ("ember_blaze_slash".equals(def.id)) {
            ok = castBlazeSlash(player, def);
        } else if ("ember_ash_familiar".equals(def.id)) {
            ok = castAshMark(player, def);
        } else if ("ember_warden_taunt".equals(def.id)) {
            ok = castWardenTaunt(player, def);
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "未知技能类型：" + def.id);
            return;
        }
        if (!ok) return;
        startCooldown(player.getUniqueId(), def.id, def.cooldownSeconds);
        player.sendMessage(PREFIX + ChatColor.GREEN + "释放 " + def.display);
    }

    private boolean castBlazeSlash(Player player, SkillDef def) {
        Location eye = player.getEyeLocation();
        Vector look = eye.getDirection().normalize();
        double cosHalf = Math.cos(Math.toRadians(Math.max(1.0, def.arcDegrees) / 2.0));
        double dmg = computeFlatDamage(player, def, def.damage);
        int hit = 0;
        for (Entity e : player.getNearbyEntities(def.range, def.range, def.range)) {
            if (!isMonsterTarget(player, e)) continue;
            LivingEntity le = (LivingEntity) e;
            Vector to = le.getEyeLocation().toVector().subtract(eye.toVector());
            double dist = to.length();
            if (dist > def.range || dist < 0.05) continue;
            double dot = look.dot(to.normalize());
            if (dot < cosHalf) continue;
            le.damage(dmg, player);
            spawnParticles(le.getLocation().add(0, 1, 0), def.particles, 12);
            hit++;
        }
        if (hit == 0) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "附近没有目标");
            return false;
        }
        playSound(player.getLocation(), def.sound);
        spawnParticles(eye.clone().add(look.clone().multiply(1.5)), def.particles, 20);
        return true;
    }

    private boolean castAshMark(Player player, SkillDef def) {
        LivingEntity target = findLookTarget(player, def.range);
        if (target == null) {
            target = findNearestHostile(player, def.range);
        }
        if (target == null) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "附近没有目标");
            return false;
        }
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW, Math.max(1, def.slowTicks), Math.max(0, def.slowAmplifier),
                false, true), true);
        long expire = System.currentTimeMillis() + Math.max(1, def.markDurationTicks) * 50L;
        ashMarks.put(player.getUniqueId(), new AshMark(
                target.getUniqueId(), expire, def.markBonusDamage, def.damageCap));
        spawnParticles(target.getLocation().add(0, 1, 0), def.particles, 16);
        playSound(target.getLocation(), def.sound);
        return true;
    }

    private boolean castWardenTaunt(Player player, SkillDef def) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.DAMAGE_RESISTANCE,
                Math.max(1, def.resistanceTicks),
                Math.max(0, def.resistanceAmplifier),
                false, true), true);
        double r = def.radius > 0 ? def.radius : 4.0;
        for (Entity e : player.getNearbyEntities(r, r, r)) {
            if (!isMonsterTarget(player, e)) continue;
            LivingEntity le = (LivingEntity) e;
            if (le.getLocation().distance(player.getLocation()) > r) continue;
            le.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOW, Math.max(1, def.slowTicks), Math.max(0, def.slowAmplifier),
                    false, true), true);
            spawnParticles(le.getLocation().add(0, 1, 0), def.particles, 8);
        }
        spawnParticles(player.getLocation().add(0, 1, 0), def.particles, 24);
        playSound(player.getLocation(), def.sound);
        return true;
    }

    private double computeFlatDamage(Player player, SkillDef def, double baseDamage) {
        int talentBonus = 0;
        TalentService talent = plugin.getTalentService();
        if (talent != null && def.talentBonusMax > 0) {
            PlayerData data = dataStore.get(player.getUniqueId());
            int count = talent.countUnlockedNodesForSkill(data, def.id);
            talentBonus = Math.min(def.talentBonusMax, count);
        }
        double raw = baseDamage + talentBonus;
        return Math.min(def.damageCap, Math.min(damageCapGlobal, raw));
    }

    private boolean isMonsterTarget(Player player, Entity e) {
        if (e == null || !(e instanceof LivingEntity)) return false;
        if (e instanceof Player) return false;
        if (e instanceof ArmorStand) return false;
        if (e.equals(player)) return false;
        LivingEntity le = (LivingEntity) e;
        return !le.isDead() && le.getHealth() > 0;
    }

    private LivingEntity findLookTarget(Player player, double range) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();
        LivingEntity best = null;
        double bestDist = range + 1;
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (!isMonsterTarget(player, e)) continue;
            LivingEntity le = (LivingEntity) e;
            Vector to = le.getEyeLocation().toVector().subtract(eye.toVector());
            double dist = to.length();
            if (dist > range || dist < 0.05) continue;
            double dot = dir.dot(to.normalize());
            if (dot < 0.90) continue;
            if (dist < bestDist) {
                bestDist = dist;
                best = le;
            }
        }
        return best;
    }

    private LivingEntity findNearestHostile(Player player, double range) {
        LivingEntity best = null;
        double bestDist = range;
        Location origin = player.getLocation();
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (!isMonsterTarget(player, e)) continue;
            double d = e.getLocation().distance(origin);
            if (d <= bestDist) {
                bestDist = d;
                best = (LivingEntity) e;
            }
        }
        return best;
    }

    private void startCooldown(UUID uuid, String skillId, int seconds) {
        if (seconds <= 0) return;
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) {
            map = new HashMap<String, Long>();
            cooldowns.put(uuid, map);
        }
        map.put(skillId, Long.valueOf(System.currentTimeMillis() + seconds * 1000L));
    }

    private long remainingCooldownMs(UUID uuid, String skillId) {
        Map<String, Long> map = cooldowns.get(uuid);
        if (map == null) return 0L;
        Long exp = map.get(skillId);
        if (exp == null) return 0L;
        long left = exp.longValue() - System.currentTimeMillis();
        if (left <= 0) {
            map.remove(skillId);
            return 0L;
        }
        return left;
    }

    private void spawnParticles(Location loc, String name, int count) {
        if (loc == null || loc.getWorld() == null || name == null || name.isEmpty()) return;
        try {
            Particle p = Particle.valueOf(name.toUpperCase());
            loc.getWorld().spawnParticle(p, loc, count, 0.35, 0.4, 0.35, 0.02);
        } catch (IllegalArgumentException ignored) {
            // unknown particle name in YAML
        }
    }

    private void playSound(Location loc, String name) {
        if (loc == null || loc.getWorld() == null || name == null || name.isEmpty()) return;
        try {
            Sound s = Sound.valueOf(name.toUpperCase());
            loc.getWorld().playSound(loc, s, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {
            // unknown sound name in YAML
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getEntity() instanceof Player) return;
        if (event.getEntity() instanceof ArmorStand) return;
        Player attacker = (Player) event.getDamager();
        AshMark mark = ashMarks.get(attacker.getUniqueId());
        if (mark == null) return;
        if (System.currentTimeMillis() > mark.expireMillis) {
            ashMarks.remove(attacker.getUniqueId());
            return;
        }
        if (!mark.victim.equals(event.getEntity().getUniqueId())) return;
        ashMarks.remove(attacker.getUniqueId());
        double add = Math.min(mark.damageCap, Math.min(damageCapGlobal, mark.bonusDamage));
        if (add > 0) {
            event.setDamage(event.getDamage() + add);
            spawnParticles(event.getEntity().getLocation().add(0, 1, 0), "SMOKE_NORMAL", 10);
        }
    }

    /** Clear in-memory CD/marks on quit (optional hygiene). */
    public void onQuit(UUID uuid) {
        if (uuid == null) return;
        cooldowns.remove(uuid);
        ashMarks.remove(uuid);
    }
}
