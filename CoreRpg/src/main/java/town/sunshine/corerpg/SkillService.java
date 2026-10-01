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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Covenant active skills — docs/ember-skills-passives.md (2026-09-27 rework).
 * Damage scales off the caster's full-charge basic hit (StatService.fullHitDamage), clamped to max_hit_mult × that hit.
 * CD in-memory only.
 */
public final class SkillService implements Listener {

    private static final String PREFIX = ChatColor.LIGHT_PURPLE + "[技能] " + ChatColor.RESET;

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean enabled = true;
    private boolean ignoreEnhanceMultiplier = true;
    private double damageCapGlobal = 12.0;
    /** 2026-09-27 rework: every skill multiplier is clamped to this × a full-charge basic hit */
    private double maxHitMult = 2.5;
    private double talentMultPerNode = 0.1;
    /** true while CoreRpg itself deals skill / passive damage (StatService + passives skip it: no double bonus, no proc loops) */
    public static boolean internalDamage = false;

    private final Map<String, SkillDef> skills = new LinkedHashMap<String, SkillDef>();
    /** covenant id -> skill id */
    private final Map<String, String> covenantToSkill = new HashMap<String, String>();

    /** UUID -> skillId -> expireMillis */
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<UUID, Map<String, Long>>();

    /** victim UUID -> ash mark (any player's damage to the victim is amplified while it lasts) */
    private final Map<UUID, AshMark> ashMarks = new HashMap<UUID, AshMark>();
    /** victim UUID -> taunting warden + expiry */
    private final Map<UUID, UUID> tauntBy = new HashMap<UUID, UUID>();
    private final Map<UUID, Long> tauntUntil = new HashMap<UUID, Long>();

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
        public double damageMult;
        public double markPct;
        public double tauntSeconds;
        public int maxTargets;

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
        final long expireMillis;
        final double pct;
        AshMark(long expireMillis, double pct) {
            this.expireMillis = expireMillis;
            this.pct = pct;
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
        maxHitMult = Math.max(0.1, cfg.getDouble("max_hit_mult", 2.5));
        talentMultPerNode = cfg.getDouble("talent_mult_per_node", 0.1);
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
            def.damageMult = sec.getDouble("damage_mult", 0.0);
            def.markPct = sec.getDouble("mark_pct", 0.0);
            def.tauntSeconds = sec.getDouble("taunt_seconds", 0.0);
            def.maxTargets = Math.max(1, sec.getInt("max_targets", 6));
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
        double basic = basicHit(player);
        int approxDmg = (int) Math.round(skillDamage(player, def));
        player.sendMessage(PREFIX + def.display + ChatColor.GRAY + " · " + cdRemain);
        player.sendMessage(ChatColor.GRAY + "  冷却 " + def.cooldownSeconds + "s"
                + " · 普攻满蓄力约 " + (int) Math.round(basic)
                + (approxDmg > 0 ? " · 技能伤害约 " + approxDmg : ""));
        if ("ember_blaze_slash".equals(def.id)) {
            player.sendMessage(ChatColor.GRAY + "  前方 " + (int) def.arcDegrees + "° 挥砍，每个目标约 "
                    + (int) Math.round(100 * effMult(player, def)) + "% 满蓄力普攻");
        } else if ("ember_ash_familiar".equals(def.id)) {
            player.sendMessage(ChatColor.GRAY + "  灰印 " + (def.markDurationTicks / 20) + "s：目标受到所有玩家伤害 +"
                    + (int) Math.round(100 * def.markPct) + "%，并减速");
        } else if ("ember_warden_taunt".equals(def.id)) {
            player.sendMessage(ChatColor.GRAY + "  抗性 " + (def.resistanceAmplifier + 1) + " 级 " + (def.resistanceTicks / 20)
                    + "s · 嘲讽 " + (int) def.radius + " 格内敌人 " + (int) def.tauntSeconds + "s");
        }
        player.sendMessage(ChatColor.DARK_GRAY + "  /corerpg skill 释放");
    }

    public void cast(Player player) {
        if (town.sunshine.corerpg.p1.EmberMode.isP1(player)) { // ember-v1.0-P1 A12/A13: shared 烬斩 only, no covenant actives
            castEmberSlashP1(player);
            return;
        }
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
        castDealt = 0;
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
        StatService stS = plugin.getStatService();
        if (stS != null && castDealt > 0) stS.skillHeal(player, castDealt);
        castDealt = 0;
        startCooldown(player.getUniqueId(), def.id, def.cooldownSeconds);
        player.sendMessage(PREFIX + ChatColor.GREEN + "释放 " + def.display);
    }

    private boolean castBlazeSlash(Player player, SkillDef def) {
        Location eye = player.getEyeLocation();
        Vector look = eye.getDirection().normalize();
        double cosHalf = Math.cos(Math.toRadians(Math.max(1.0, def.arcDegrees) / 2.0));
        double dmg = skillDamage(player, def);
        int hit = 0;
        for (Entity e : player.getNearbyEntities(def.range, def.range, def.range)) {
            if (hit >= def.maxTargets) break;
            if (!isMonsterTarget(player, e)) continue;
            LivingEntity le = (LivingEntity) e;
            Vector to = le.getEyeLocation().toVector().subtract(eye.toVector());
            double dist = to.length();
            if (dist > def.range || dist < 0.05) continue;
            double dot = look.dot(to.normalize());
            if (dot < cosHalf) continue;
            dealSkill(player, le, dmg);
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

    /**
     * ember-v1.0-P1 A12 烬斩 (策划书 §4.2): 1.5B, 8 s CD, 100° front arc, 3.5 blocks, max 5 targets sorted by
     * distance then entity id; no crit, no life steal, no talent bonus, no set count. The CD lives in
     * EmberPlayerState (survives reconnect, and restart with MySQL).
     */
    private void castEmberSlashP1(Player player) {
        town.sunshine.corerpg.p1.EmberMode mode = town.sunshine.corerpg.p1.EmberMode.get();
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (mode == null || ls == null) return;
        town.sunshine.corerpg.p1.EmberPlayerState st = ls.state(player.getUniqueId());
        long now = System.currentTimeMillis();
        if (st.skillCdUntil > now) {
            player.sendMessage(PREFIX + ChatColor.RED + "烬斩冷却中，剩余 " + (int) Math.ceil((st.skillCdUntil - now) / 1000.0) + "s");
            return;
        }
        double range = mode.d("skill.radius", 3.5);
        double arc = mode.d("skill.arc_degrees", 100.0);
        int maxTargets = Math.max(1, mode.i("skill.max_targets", 5));
        int cd = Math.max(0, mode.i("skill.cooldown_seconds", 8));
        Location eye = player.getEyeLocation();
        Vector look = eye.getDirection().normalize();
        double cosHalf = Math.cos(Math.toRadians(Math.max(1.0, arc) / 2.0));
        List<LivingEntity> targets = new ArrayList<LivingEntity>();
        final Map<LivingEntity, Double> distOf = new HashMap<LivingEntity, Double>();
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (!isMonsterTarget(player, e)) continue;
            LivingEntity le = (LivingEntity) e;
            Vector to = le.getEyeLocation().toVector().subtract(eye.toVector());
            double dist = to.length();
            if (dist > range || dist < 0.05) continue;
            if (look.dot(to.normalize()) < cosHalf) continue;
            targets.add(le);
            distOf.put(le, dist);
        }
        if (targets.isEmpty()) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "附近没有目标");
            return;
        }
        java.util.Collections.sort(targets, new java.util.Comparator<LivingEntity>() {
            @Override public int compare(LivingEntity a, LivingEntity b) {
                int c = Double.compare(distOf.get(a), distOf.get(b));
                return c != 0 ? c : Integer.compare(a.getEntityId(), b.getEntityId());
            }
        });
        double b = ls.get(player).b;
        double dmg = town.sunshine.corerpg.p1.EmberFormula.skill(town.sunshine.corerpg.p1.EmberMode.tables(), b);
        SkillDef look2 = getSkill("ember_blaze_slash");
        String prevTag = town.sunshine.corerpg.p1.EmberCombatListener.internalTag;
        town.sunshine.corerpg.p1.EmberCombatListener.internalTag = String.format(java.util.Locale.ROOT, "A12 烬斩 1.5×B(%.2f)=%.2f", b, dmg);
        try {
            int n = 0;
            for (LivingEntity le : targets) {
                if (n++ >= maxTargets) break;
                dealInternal(player, le, dmg);
                if (look2 != null) spawnParticles(le.getLocation().add(0, 1, 0), look2.particles, 12);
            }
        } finally {
            town.sunshine.corerpg.p1.EmberCombatListener.internalTag = prevTag;
        }
        if (look2 != null) {
            playSound(player.getLocation(), look2.sound);
            spawnParticles(eye.clone().add(look.clone().multiply(1.5)), look2.particles, 20);
        }
        st.skillCdUntil = now + cd * 1000L;
        ls.saveState(player);
        player.sendMessage(PREFIX + ChatColor.GREEN + "释放 烬斩");
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
        ashMarks.put(target.getUniqueId(), new AshMark(expire, Math.min(def.markPct + talentBonus(player, def) * 0.02, maxHitMult - 1.0)));
        double strike = skillDamage(player, def);
        if (strike > 0) dealSkill(player, target, strike);
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
        int n = 0;
        for (Entity e : player.getNearbyEntities(r, r, r)) {
            if (!isMonsterTarget(player, e)) continue;
            LivingEntity le = (LivingEntity) e;
            if (le.getLocation().distance(player.getLocation()) > r) continue;
            if (n++ >= def.maxTargets) break;
            le.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOW, Math.max(1, def.slowTicks), Math.max(0, def.slowAmplifier),
                    false, true), true);
            if (def.tauntSeconds > 0 && le instanceof org.bukkit.entity.Creature) {
                ((org.bukkit.entity.Creature) le).setTarget(player);
                tauntBy.put(le.getUniqueId(), player.getUniqueId());
                tauntUntil.put(le.getUniqueId(), Long.valueOf(System.currentTimeMillis() + (long) (def.tauntSeconds * 1000)));
            }
            double wave = skillDamage(player, def);
            if (wave > 0) dealSkill(player, le, wave);
            spawnParticles(le.getLocation().add(0, 1, 0), def.particles, 8);
        }
        spawnParticles(player.getLocation().add(0, 1, 0), def.particles, 24);
        playSound(player.getLocation(), def.sound);
        return true;
    }

    private int talentBonus(Player player, SkillDef def) {
        TalentService talent = plugin.getTalentService();
        if (talent == null || def.talentBonusMax <= 0) return 0;
        PlayerData data = dataStore.get(player.getUniqueId());
        return Math.min(def.talentBonusMax, talent.countUnlockedNodesForSkill(data, def.id));
    }

    /** Full-charge basic hit of this player right now (weapon + Sharpness + gear stats), from StatService. */
    public double basicHit(Player player) {
        StatService st = plugin.getStatService();
        return st != null ? st.fullHitDamage(player) : 1.0;
    }

    private double effMult(Player player, SkillDef def) {
        if (def.damageMult <= 0) return 0;
        return Math.min(maxHitMult, def.damageMult + talentMultPerNode * talentBonus(player, def));
    }

    /** Skill damage = damage_mult (+ talent) × full-charge basic hit, clamped to max_hit_mult × basic hit. */
    public double skillDamage(Player player, SkillDef def) {
        return effMult(player, def) * basicHit(player);
    }

    /**
     * Deal CoreRpg-owned damage as a player attack (MythicMobs modifiers / calamity scaling still apply) without
     * eating the victim's hurt i-frames: noDamageTicks + lastDamage are restored so the player's next swing lands normally.
     */
    /** 1.13.0: skill damage (not passives) — tracked per cast for the covenant skill life steal. */
    private double castDealt = 0;
    private void dealSkill(Player player, LivingEntity le, double dmg) {
        if (le == null || le.isDead() || dmg <= 0) return;
        double before = le.getHealth();
        dealInternal(player, le, dmg);
        castDealt += Math.max(0, before - (le.isDead() ? 0 : le.getHealth()));
    }

    public static void dealInternal(Player player, LivingEntity le, double dmg) {
        if (le == null || le.isDead() || dmg <= 0) return;
        int ndt = le.getNoDamageTicks();
        double last = le.getLastDamage();
        boolean prev = internalDamage;
        internalDamage = true;
        try {
            le.setNoDamageTicks(0);
            le.damage(dmg, player);
        } finally {
            internalDamage = prev;
        }
        if (!le.isDead()) {
            le.setNoDamageTicks(ndt);
            le.setLastDamage(last);
        }
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

    /** 灰印: any player damage to a marked mob × (1 + pct). HIGH so it multiplies the finished hit (after stat flat bonus). */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (ashMarks.isEmpty()) return;
        if (town.sunshine.corerpg.p1.EmberMode.isP1(event.getEntity())) return; // ember-v1.0-P1 A14: no 灰印 multiplier
        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player) return;
        Entity d = event.getDamager();
        if (d instanceof org.bukkit.entity.Projectile && ((org.bukkit.entity.Projectile) d).getShooter() instanceof Player) {
            d = (Player) ((org.bukkit.entity.Projectile) d).getShooter();
        }
        if (!(d instanceof Player)) return;
        AshMark mark = ashMarks.get(event.getEntity().getUniqueId());
        if (mark == null) return;
        if (System.currentTimeMillis() > mark.expireMillis) {
            ashMarks.remove(event.getEntity().getUniqueId());
            return;
        }
        event.setDamage(event.getDamage() * (1.0 + mark.pct));
    }

    /** Taunt: while it lasts, a taunted mob that switches to someone else is pointed back at the warden. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRetarget(org.bukkit.event.entity.EntityTargetLivingEntityEvent event) {
        if (tauntBy.isEmpty()) return;
        UUID mob = event.getEntity().getUniqueId();
        UUID w = tauntBy.get(mob);
        if (w == null) return;
        Long until = tauntUntil.get(mob);
        if (until == null || System.currentTimeMillis() > until.longValue()) {
            tauntBy.remove(mob);
            tauntUntil.remove(mob);
            return;
        }
        Player warden = Bukkit.getPlayer(w);
        if (warden == null || !warden.isOnline() || warden.isDead() || !warden.getWorld().equals(event.getEntity().getWorld())) return;
        if (event.getTarget() == null || !event.getTarget().getUniqueId().equals(w)) event.setTarget(warden);
    }

    /** Clear in-memory CD/marks on quit (optional hygiene). */
    public void onQuit(UUID uuid) {
        if (uuid == null) return;
        cooldowns.remove(uuid);
    }
}
