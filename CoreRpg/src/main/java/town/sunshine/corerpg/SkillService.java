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
        String act = args.length >= 2 ? args[1].toLowerCase(java.util.Locale.ROOT) : "";
        if ("info".equals(act) || "kit".equals(act) || "技能组".equals(act) || "组".equals(act)) {
            // Hub is outside P1 combat scope; kit UI still uses active() (D211)
            if (town.sunshine.corerpg.p1.EmberMode.active()) { cmdKitInfo(player); return; }
            cmdInfo(player);
            return;
        }
        if ("shape".equals(act) || "符文".equals(act) || "形状".equals(act)) {
            cmdShape(player, args.length >= 3 ? args[2] : null);
            return;
        }
        if ("dir".equals(act) || "stepdir".equals(act) || "方向".equals(act) || "后撤".equals(act) || "前冲".equals(act)) {
            String raw = args.length >= 3 ? args[2] : null;
            if (raw == null && ("后撤".equals(act) || "前冲".equals(act))) raw = act;
            cmdStepDir(player, raw);
            return;
        }
        cast(player);
    }

    /** D211: P1 skill-kit page text (also driven by TrMenu ember_skill_kit). */
    public void cmdKitInfo(Player player) {
        town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
        PlayerData data = dataStore.get(player.getUniqueId());
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        town.sunshine.corerpg.p1.EmberPlayerState st = ls == null ? null : ls.state(player.getUniqueId());
        long now = System.currentTimeMillis();
        long left = st == null ? 0 : Math.max(0, st.skillCdUntil - now);
        String charge = left > 0
                ? ChatColor.RED + "充能中 " + (int) Math.ceil(left / 1000.0) + "s"
                : ChatColor.GREEN + "就绪";
        boolean dash = town.sunshine.corerpg.p1.EmberSkillKit.dashUnlocked(data, runs);
        boolean shapes = town.sunshine.corerpg.p1.EmberSkillKit.shapeUnlocked(data, runs);
        town.sunshine.corerpg.p1.EmberGrowthService gsv = town.sunshine.corerpg.p1.EmberGrowthService.get();
        town.sunshine.corerpg.p1.EmberGrowth.Mods gmods = gsv == null ? town.sunshine.corerpg.p1.EmberGrowth.Mods.NONE : gsv.mods(player);
        town.sunshine.corerpg.p1.EmberSkillKit.Shape sh = town.sunshine.corerpg.p1.EmberSkillKit.resolve(
                player, data, runs, town.sunshine.corerpg.p1.EmberMode.get(), gmods);
        player.sendMessage(PREFIX + ChatColor.GOLD + "余烬技能组");
        player.sendMessage(ChatColor.GRAY + "  余烬充能（烬斩 / 烬突共用）· " + charge);
        String slashHint = slashSetTip(player);
        player.sendMessage(ChatColor.YELLOW + "  F" + ChatColor.GRAY + " 烬斩 · 形状 "
                + ChatColor.WHITE + sh.label
                + (sh.sigOverride ? ChatColor.DARK_GRAY + "（签名覆盖符文）" : "")
                + (slashHint != null ? ChatColor.GRAY + slashHint : ""));
        if (dash) {
            String dashId = "";
            town.sunshine.corerpg.p1.EmberLoadoutService _ls = plugin.getEmberLoadouts();
            if (_ls != null) {
                String sf = _ls.get(player).activeSet;
                if ("scorch".equals(sf)) dashId = " · 烬途点燃";
                else if ("burst".equals(sf)) dashId = " · 烬途缓速";
                else if ("sustain".equals(sf)) dashId = " · 烬途微疗";
            }
            player.sendMessage(ChatColor.YELLOW + "  潜行+F" + ChatColor.GRAY + " 烬突 · 冲 4 格 · 最多 3 个各 1.5B（首领×0.5）· 花掉本次充能" + dashId);
        } else {
            player.sendMessage(ChatColor.DARK_GRAY + "  潜行+F 烬突 · 首通 Q02 后解锁（此前仍放烬斩）");
        }
        boolean stepUnlock = town.sunshine.corerpg.p1.EmberSkillKit.stepVariantUnlocked(data, runs);
        town.sunshine.corerpg.p1.EmberLoadout lo = ls == null ? null : ls.get(player);
        String fam = lo == null ? "none" : lo.activeSet;
        int stepVar = town.sunshine.corerpg.p1.EmberSkillKit.stepSetVariant(data, runs, fam);
        boolean back = town.sunshine.corerpg.p1.EmberSkillKit.stepBackward(data);
        String stepName = town.sunshine.corerpg.p1.EmberSkillKit.stepDisplayName(stepVar, back);
        String ignite = "";
        if (stepVar == town.sunshine.corerpg.p1.EmberSkillKit.STEP_VARIANT_HUOHEN)
            ignite = back ? " · 起跳点燃 1（焚烬同系数）" : " · 落点点燃 1（焚烬同系数）";
        else if (stepVar == town.sunshine.corerpg.p1.EmberSkillKit.STEP_VARIANT_BAOSHAN)
            ignite = back ? " · 起跳缓速 I 1.5s" : " · 落点缓速 I 1.5s";
        else if (stepVar == town.sunshine.corerpg.p1.EmberSkillKit.STEP_VARIANT_CHENGHU)
            ignite = " · 落点微疗 0.5%H";
        String distNote = back ? "后撤 4 格" : "前冲 5 格";
        player.sendMessage(ChatColor.YELLOW + "  潜行+Q" + ChatColor.GRAY + " " + stepName
                + " · 14 秒 · " + distNote + ignite);
        if (stepVar == town.sunshine.corerpg.p1.EmberSkillKit.STEP_VARIANT_PLAIN && stepUnlock) {
            player.sendMessage(ChatColor.DARK_GRAY + "    两件套自动变：焚烬火痕 / 烬爆爆闪 / 承烬承护");
        } else if (stepVar == town.sunshine.corerpg.p1.EmberSkillKit.STEP_VARIANT_PLAIN && !stepUnlock) {
            player.sendMessage(ChatColor.DARK_GRAY + "    套装身法：首通 Q05 + 对应两件套");
        }
        player.sendMessage(ChatColor.GRAY + "  身法方向：前冲 / 后撤 · 当前 "
                + ChatColor.WHITE + town.sunshine.corerpg.p1.EmberSkillKit.stepDirName(
                        town.sunshine.corerpg.p1.EmberSkillKit.stepDirId(data))
                + ChatColor.DARK_GRAY + " · 出本点技能页切换（切换后身法冷却转满）");
        if (shapes) {
            int id = town.sunshine.corerpg.p1.EmberSkillKit.shapeId(data, runs);
            player.sendMessage(ChatColor.GRAY + "  符文：扇形 / 直线 / 环斩 · 当前 "
                    + ChatColor.WHITE + town.sunshine.corerpg.p1.EmberSkillKit.shapeName(id)
                    + ChatColor.DARK_GRAY + " · 出本点技能页切换（切换后充能转满）");
        } else {
            player.sendMessage(ChatColor.DARK_GRAY + "  烬斩符文 · 首通 Q04 后解锁");
        }
        boolean parry = town.sunshine.corerpg.p1.EmberSkillKit.parryUnlocked(data, runs);
        if (parry) {
            town.sunshine.corerpg.p1.EmberParry ep = town.sunshine.corerpg.p1.EmberParry.get();
            int pLeft = ep == null ? 0 : ep.cdLeftSeconds(player.getUniqueId());
            String pCd = pLeft > 0 ? (ChatColor.RED + "冷却 " + pLeft + "s") : (ChatColor.GREEN + "就绪");
            player.sendMessage(ChatColor.YELLOW + "  Q" + ChatColor.GRAY + " "
                    + town.sunshine.corerpg.p1.EmberSkillKit.DISPLAY_PARRY
                    + " · 预警砸中你后半秒内按 · 反打 0.45B · 独立 28s · " + pCd);
            player.sendMessage(ChatColor.DARK_GRAY + "    好：对齐落地窗口 · 坏：窗外乱按白丢 CD · 不挡预警伤");
        } else {
            player.sendMessage(ChatColor.DARK_GRAY + "  守招·余烬招架 · 首通 Q03 后解锁（单按 Q）");
        }
    }

    /** D211: /corerpg skill shape <fan|line|ring> — hub only; puts 烬斩 on full CD (X1). */
    public void cmdShape(Player player, String raw) {
        if (!town.sunshine.corerpg.p1.EmberMode.active()) {
            player.sendMessage(PREFIX + ChatColor.RED + "仅 P1 可用");
            return;
        }
        town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
        PlayerData data = dataStore.get(player.getUniqueId());
        if (!town.sunshine.corerpg.p1.EmberSkillKit.shapeUnlocked(data, runs)) {
            player.sendMessage(PREFIX + ChatColor.RED + "烬斩符文未解锁（首通 Q04）");
            return;
        }
        if (town.sunshine.corerpg.p1.EmberSkillKit.inDungeon(player, runs)) {
            player.sendMessage(PREFIX + ChatColor.RED + "副本里不能换符文，请回城后再换");
            return;
        }
        if (raw == null || raw.isEmpty()) {
            int id = town.sunshine.corerpg.p1.EmberSkillKit.shapeId(data, runs);
            player.sendMessage(PREFIX + "当前烬斩符文：" + ChatColor.WHITE
                    + town.sunshine.corerpg.p1.EmberSkillKit.shapeName(id)
                    + ChatColor.GRAY + " · 用法 /corerpg skill shape <fan|line|ring>");
            return;
        }
        int id = town.sunshine.corerpg.p1.EmberSkillKit.parseShape(raw);
        if (id < 0) {
            player.sendMessage(PREFIX + ChatColor.RED + "未知形状 · fan / line / ring");
            return;
        }
        boolean changed = town.sunshine.corerpg.p1.EmberSkillKit.setShape(data, id);
        dataStore.flushMutation(player.getUniqueId());
        // X1: swapping always puts the shared charge on full CD
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        town.sunshine.corerpg.p1.EmberMode mode = town.sunshine.corerpg.p1.EmberMode.get();
        int cd = Math.max(0, mode == null ? 8 : mode.i("skill.cooldown_seconds", 8));
        if (ls != null) {
            town.sunshine.corerpg.p1.EmberPlayerState st = ls.state(player.getUniqueId());
            st.skillCdUntil = System.currentTimeMillis() + cd * 1000L;
            ls.saveState(player);
            town.sunshine.corerpg.p1.EmberSlashReady.arm(player, st.skillCdUntil); // D479
        }
        if (changed) {
            player.sendMessage(PREFIX + ChatColor.GREEN + "烬斩符文 → "
                    + town.sunshine.corerpg.p1.EmberSkillKit.shapeName(id)
                    + ChatColor.GRAY + " · 充能转满（" + cd + "s）");
        } else {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "已是 "
                    + town.sunshine.corerpg.p1.EmberSkillKit.shapeName(id)
                    + ChatColor.GRAY + " · 充能仍转满（" + cd + "s）");
        }
    }

    /** D219: /corerpg skill dir <forward|back> — hub only; puts 身法 on full CD. */
    public void cmdStepDir(Player player, String raw) {
        if (!town.sunshine.corerpg.p1.EmberMode.active()) {
            player.sendMessage(PREFIX + ChatColor.RED + "仅 P1 可用");
            return;
        }
        town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
        PlayerData data = dataStore.get(player.getUniqueId());
        if (town.sunshine.corerpg.p1.EmberSkillKit.inDungeon(player, runs)) {
            player.sendMessage(PREFIX + ChatColor.RED + "副本里不能换身法方向，请回城后再换");
            return;
        }
        if (raw == null || raw.isEmpty()) {
            int id = town.sunshine.corerpg.p1.EmberSkillKit.stepDirId(data);
            player.sendMessage(PREFIX + "当前身法方向：" + ChatColor.WHITE
                    + town.sunshine.corerpg.p1.EmberSkillKit.stepDirName(id)
                    + ChatColor.GRAY + " · 用法 /corerpg skill dir <forward|back>");
            return;
        }
        int id = town.sunshine.corerpg.p1.EmberSkillKit.parseStepDir(raw);
        if (id < 0) {
            player.sendMessage(PREFIX + ChatColor.RED + "未知方向 · forward / back（前冲 / 后撤）");
            return;
        }
        boolean changed = town.sunshine.corerpg.p1.EmberSkillKit.setStepDir(data, id);
        dataStore.flushMutation(player.getUniqueId());
        FlexSkillService flex = plugin.getFlexSkillService();
        int cd = 14;
        if (flex != null) {
            FlexSkillService.FlexDef def = data.hasFlexSkill() ? flex.getFlex(data.getFlexSkillId()) : null;
            if (def != null) cd = Math.max(0, def.cooldownSeconds);
            flex.putEquippedOnFullCooldown(player);
        }
        if (changed) {
            player.sendMessage(PREFIX + ChatColor.GREEN + "身法方向 → "
                    + town.sunshine.corerpg.p1.EmberSkillKit.stepDirName(id)
                    + ChatColor.GRAY + " · 身法冷却转满（" + cd + "s）");
        } else {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "已是 "
                    + town.sunshine.corerpg.p1.EmberSkillKit.stepDirName(id)
                    + ChatColor.GRAY + " · 身法冷却仍转满（" + cd + "s）");
        }
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
     * B2.175 / D34 (策划书 §3.1「默认有可用主动」, §19.1「玩家不需要手打命令」): in a P1 world, the swap-hands key (F)
     * while the main hand holds the player's valid P1 blade casts 烬斩 instead of swapping. The off hand never counts
     * in P1 (§4.1), so nothing is lost; outside P1 worlds, or without a valid P1 blade, F swaps as before.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSwapHotkeyP1(org.bukkit.event.player.PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player == null || !town.sunshine.corerpg.p1.EmberMode.isP1(player)) return;
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        town.sunshine.corerpg.p1.EmberLoadout l = ls == null ? null : ls.get(player);
        if (l == null || l.blade == null) return;
        event.setCancelled(true);
        // D211 / X12: sneak+F = 烬突 after Q02 first clear; before that (and bare F) still 烬斩
        if (player.isSneaking()) {
            town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
            PlayerData data = dataStore.get(player.getUniqueId());
            if (town.sunshine.corerpg.p1.EmberSkillKit.dashUnlocked(data, runs)) {
                castEmberDashP1(player);
                return;
            }
        }
        castEmberSlashP1(player);
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
        // D174 signature + D211 rune shapes (EmberSkillKit.resolve: signature line/ring/charge overrides the chosen rune)
        town.sunshine.corerpg.p1.EmberGrowthService gsv = town.sunshine.corerpg.p1.EmberGrowthService.get();
        town.sunshine.corerpg.p1.EmberGrowth.Mods gmods = gsv == null ? town.sunshine.corerpg.p1.EmberGrowth.Mods.NONE : gsv.mods(player);
        boolean variant = gmods.get("skill_var") > 0;
        town.sunshine.corerpg.p1.EmberSkillKit.Shape sh = town.sunshine.corerpg.p1.EmberSkillKit.resolve(
                player, dataStore.get(player.getUniqueId()), plugin.getEmberRuns(), mode, gmods);
        int cd = Math.max(0, mode.i("skill.cooldown_seconds", 8));
        final double fRange = sh.range, fArc = sh.arc, fLine = sh.line;
        final int fMax = sh.maxTargets;
        if (slashTargets(player, fRange, fArc, fLine).isEmpty()) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "附近没有目标");
            return;
        }
        // D174 stage 3 L13 炉锁巨锤 (p1sim skill_variant skill_charge): wind-up — the CD starts now, the hit lands after
        // skill_charge seconds on whoever is in the shape then; no melee meanwhile (EmberCombatListener.onMelee cancels)
        double charge = variant ? gmods.get("skill_charge") : 0.0;
        st.skillCdUntil = now + cd * 1000L;
        ls.saveState(player);
        town.sunshine.corerpg.p1.EmberSlashReady.arm(player, st.skillCdUntil); // D479 shared charge ready
        if (charge > 0) {
            final java.util.UUID id = player.getUniqueId();
            final town.sunshine.corerpg.p1.EmberGrowth.Mods fMods = gmods;
            final long until = now + Math.round(charge * 1000.0);
            CHARGING.put(id, until);
            player.sendMessage(PREFIX + ChatColor.GOLD + String.format(java.util.Locale.ROOT, "烬斩蓄力 %.1fs（蓄力时普攻无效）", charge));
            playSound(player.getLocation(), "BLOCK_BEACON_POWER_SELECT");
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Long u = CHARGING.get(id);
                if (u != null && u == until) CHARGING.remove(id);
                if (!player.isOnline() || player.isDead() || !town.sunshine.corerpg.p1.EmberMode.isP1(player)) return;
                List<LivingEntity> t2 = slashTargets(player, fRange, fArc, fLine);
                if (t2.isEmpty()) { player.sendMessage(PREFIX + ChatColor.YELLOW + "烬斩蓄力落空（范围里没有目标了）"); return; }
                landEmberSlash(player, ls, t2, fMax, fMods, true, gsv);
                player.sendMessage(PREFIX + ChatColor.GREEN + "释放 烬斩（蓄力）");
            }, Math.max(1L, Math.round(charge * 20.0)));
            return;
        }
        List<LivingEntity> landed = slashTargets(player, fRange, fArc, fLine);
        landEmberSlash(player, ls, landed, fMax, gmods, variant, gsv);
        String tip = sh.sigOverride || sh.id != town.sunshine.corerpg.p1.EmberSkillKit.SHAPE_FAN
                ? "释放 烬斩（" + sh.label + "）" : "释放 烬斩";
        String slashId = slashSetTip(player);
        player.sendMessage(PREFIX + ChatColor.GREEN + tip
                + (slashId != null ? ChatColor.GRAY + slashId : ""));
    }

    /**
     * D211 烬突 (DESIGN §3 DS15): spends the shared 烬斩 charge, dash 4 blocks (EmberDash = 踏步 collision/doors),
     * hits ≤3 enemies along the path for 1.5B each (bosses ×0.5). No crit / lifesteal / set count; signature
     * "on 烬斩 hit" effects (L08/L11/L14) do NOT apply (X11).
     */
    private void castEmberDashP1(Player player) {
        town.sunshine.corerpg.p1.EmberMode mode = town.sunshine.corerpg.p1.EmberMode.get();
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (mode == null || ls == null) return;
        town.sunshine.corerpg.p1.EmberPlayerState st = ls.state(player.getUniqueId());
        long now = System.currentTimeMillis();
        if (st.skillCdUntil > now) {
            player.sendMessage(PREFIX + ChatColor.RED + "余烬充能中，剩余 " + (int) Math.ceil((st.skillCdUntil - now) / 1000.0) + "s");
            return;
        }
        Location from = player.getLocation();
        Location dest = town.sunshine.corerpg.p1.EmberDash.tryDash(player, town.sunshine.corerpg.p1.EmberSkillKit.DASH_DISTANCE);
        if (dest == null) {
            player.sendMessage(PREFIX + ChatColor.YELLOW + "前方受阻，无法烬突");
            return;
        }
        int cd = Math.max(0, mode.i("skill.cooldown_seconds", 8));
        st.skillCdUntil = now + cd * 1000L;
        ls.saveState(player);
        town.sunshine.corerpg.p1.EmberSlashReady.arm(player, st.skillCdUntil); // D479 shared charge ready

        double b = ls.get(player).b;
        double base = town.sunshine.corerpg.p1.EmberFormula.skill(town.sunshine.corerpg.p1.EmberMode.tables(), b);
        town.sunshine.corerpg.p1.EmberRunService runs = plugin.getEmberRuns();
        java.util.LinkedHashSet<LivingEntity> hitSet = new java.util.LinkedHashSet<LivingEntity>();
        for (Location sample : town.sunshine.corerpg.p1.EmberDash.pathSamples(from, dest)) {
            if (sample.getWorld() == null) continue;
            double r = town.sunshine.corerpg.p1.EmberSkillKit.DASH_HIT_RADIUS;
            for (Entity e : sample.getWorld().getNearbyEntities(sample, r, r, r)) {
                if (!isMonsterTarget(player, e)) continue;
                if (e.getLocation().distanceSquared(sample) > r * r) continue;
                hitSet.add((LivingEntity) e);
            }
        }
        List<LivingEntity> ordered = new ArrayList<LivingEntity>(hitSet);
        final Location origin = from;
        java.util.Collections.sort(ordered, new java.util.Comparator<LivingEntity>() {
            @Override public int compare(LivingEntity a, LivingEntity b2) {
                int c = Double.compare(a.getLocation().distanceSquared(origin), b2.getLocation().distanceSquared(origin));
                return c != 0 ? c : Integer.compare(a.getEntityId(), b2.getEntityId());
            }
        });

        spawnParticles(from.clone().add(0, 0.2, 0), "FLAME", 12);
        player.teleport(dest);
        maybeFlashDashLand(player); // D445 烬突落地 (成功位移；前方受阻路径不进此分支)
        spawnParticles(dest.clone().add(0, 0.2, 0), "FLAME", 18);
        playSound(dest, "ENTITY_ENDERDRAGON_FLAP");

        String prevTag = town.sunshine.corerpg.p1.EmberCombatListener.internalTag;
        int n = 0;
        try {
            for (LivingEntity le : ordered) {
                if (n >= town.sunshine.corerpg.p1.EmberSkillKit.DASH_MAX_TARGETS) break;
                boolean boss = runs != null && runs.isRunBoss(le);
                double dmg = base * (boss ? town.sunshine.corerpg.p1.EmberSkillKit.DASH_BOSS_MULT : 1.0);
                town.sunshine.corerpg.p1.EmberCombatListener.internalTag = String.format(java.util.Locale.ROOT,
                        "D211 烬突 1.5×B(%.2f)%s=%.2f", b, boss ? "×0.5" : "", dmg);
                town.sunshine.corerpg.p1.EmberCombatListener.dealP1(player, le, dmg,
                        town.sunshine.corerpg.p1.EmberSetEngine.Kind.SKILL,
                        town.sunshine.corerpg.p1.EmberCombatListener.internalTag);
                spawnParticles(le.getLocation().add(0, 1, 0), "FLAME", 10);
                n++;
            }
        } finally {
            town.sunshine.corerpg.p1.EmberCombatListener.internalTag = prevTag;
        }
        // D436 set identity (S0 ✅/🟡): scorch ignite×0.12 · burst Slow I 1.5s · sustain heal 0.5%H
        String famHint = applyDashSetIdentity(player, ordered, n);
        player.sendMessage(PREFIX + ChatColor.GREEN + "释放 烬突"
                + (n > 0 ? ChatColor.GRAY + " · 命中 " + n : ChatColor.DARK_GRAY + " · 未命中")
                + (famHint != null ? ChatColor.GRAY + famHint : ""));
    }

    /**
     * D436: set-bound dash land effects. Does not change DS15 base mult / CD / target cap.
     * @return tip fragment or null
     */
    private String applyDashSetIdentity(Player player, List<LivingEntity> ordered, int hitCount) {
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (ls == null) return null;
        String fam = ls.get(player).activeSet;
        if (fam == null || "none".equals(fam)) return null;
        if ("scorch".equals(fam)) {
            town.sunshine.corerpg.p1.EmberSetService sets = plugin.getEmberSets();
            if (sets != null && hitCount > 0 && town.sunshine.corerpg.p1.EmberMode.isP1(player)) {
                int lim = Math.min(hitCount, town.sunshine.corerpg.p1.EmberSkillKit.DASH_MAX_TARGETS);
                for (int i = 0; i < lim; i++) {
                    sets.skillIgnite(player, ordered.get(i),
                            town.sunshine.corerpg.p1.EmberSkillKit.DASH_IGNITE_SCALE);
                }
            }
            return " · 烬途点燃";
        }
        if ("burst".equals(fam)) {
            if (hitCount > 0 && town.sunshine.corerpg.p1.EmberMode.isP1(player)) {
                int ticks = town.sunshine.corerpg.p1.EmberSkillKit.DASH_BURST_SLOW_TICKS;
                int lim = Math.min(hitCount, town.sunshine.corerpg.p1.EmberSkillKit.DASH_MAX_TARGETS);
                for (int i = 0; i < lim; i++) {
                    ordered.get(i).addPotionEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.SLOW, ticks, 0, false, true), true);
                }
                spawnParticles(player.getLocation().clone().add(0, 0.3, 0), "CRIT", 10);
            }
            return " · 烬途缓速";
        }
        if ("sustain".equals(fam)) {
            if (town.sunshine.corerpg.p1.EmberMode.isP1(player)) {
                double heal = Math.max(0.5, player.getMaxHealth()
                        * town.sunshine.corerpg.p1.EmberSkillKit.DASH_HEAL_PCT);
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + heal));
                spawnParticles(player.getLocation().clone().add(0, 0.5, 0), "HEART", 3);
            }
            return " · 烬途微疗";
        }
        return null;
    }

    /** D211: AFK / menus — resolve the effective 烬斩 shape for this player right now. */
    public town.sunshine.corerpg.p1.EmberSkillKit.Shape resolveSlashShape(Player player) {
        town.sunshine.corerpg.p1.EmberGrowthService gsv = town.sunshine.corerpg.p1.EmberGrowthService.get();
        town.sunshine.corerpg.p1.EmberGrowth.Mods gmods = gsv == null ? town.sunshine.corerpg.p1.EmberGrowth.Mods.NONE : gsv.mods(player);
        return town.sunshine.corerpg.p1.EmberSkillKit.resolve(
                player, dataStore.get(player.getUniqueId()), plugin.getEmberRuns(),
                town.sunshine.corerpg.p1.EmberMode.get(), gmods);
    }

    /** D174 stage 3: uuid → wind-up end (ms) of a charged 烬斩 (L13); melee is cancelled until then */
    private static final Map<java.util.UUID, Long> CHARGING = new java.util.concurrent.ConcurrentHashMap<java.util.UUID, Long>();

    /** D445: last skill hit-confirm flash time (ms) per player — shared slash/dash debounce. */
    private final Map<java.util.UUID, Long> lastSkillHitFlashAt = new java.util.concurrent.ConcurrentHashMap<java.util.UUID, Long>();
    /** D445: debounce ≥0.4s between skill-confirm flashes. */
    static final long SKILL_HIT_FLASH_DEBOUNCE_MS = 400L;

    public static boolean isCharging(java.util.UUID id) {
        Long u = id == null ? null : CHARGING.get(id);
        if (u == null) return false;
        if (u > System.currentTimeMillis()) return true;
        CHARGING.remove(id, u);
        return false;
    }

    /** 烬斩 targets in the shape, sorted by distance then entity id (the cap is applied when landing) */
    private List<LivingEntity> slashTargets(Player player, double range, double arc, double line) {
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
            if (line > 0) { // narrow line: in front, within 1.0 block of the look ray
                double along = look.dot(to);
                if (along <= 0 || to.clone().subtract(look.clone().multiply(along)).length() > 1.0) continue;
            } else if (arc < 360.0 && look.dot(to.normalize()) < cosHalf) continue;
            targets.add(le);
            distOf.put(le, dist);
        }
        java.util.Collections.sort(targets, new java.util.Comparator<LivingEntity>() {
            @Override public int compare(LivingEntity a, LivingEntity b) {
                int c = Double.compare(distOf.get(a), distOf.get(b));
                return c != 0 ? c : Integer.compare(a.getEntityId(), b.getEntityId());
            }
        });
        return targets;
    }

    private void landEmberSlash(Player player, town.sunshine.corerpg.p1.EmberLoadoutService ls, List<LivingEntity> targets, int maxTargets,
                                town.sunshine.corerpg.p1.EmberGrowth.Mods gmods, boolean variant, town.sunshine.corerpg.p1.EmberGrowthService gsv) {
        Location eye = player.getEyeLocation();
        Vector look = eye.getDirection().normalize();
        double b = ls.get(player).b;
        double dmg = town.sunshine.corerpg.p1.EmberFormula.skill(town.sunshine.corerpg.p1.EmberMode.tables(), b);
        if (variant) dmg *= gmods.get("skill_mult");
        SkillDef look2 = getSkill("ember_blaze_slash");
        String prevTag = town.sunshine.corerpg.p1.EmberCombatListener.internalTag;
        town.sunshine.corerpg.p1.EmberCombatListener.internalTag = String.format(java.util.Locale.ROOT, "A12 烬斩 1.5×B(%.2f)=%.2f", b, dmg);
        List<LivingEntity> hit = new ArrayList<LivingEntity>();
        try {
            int n = 0;
            int igniteN = variant && gmods.get("skill_ignite") > 0 ? (int) Math.round(gmods.get("skill_ignite_n") > 0 ? gmods.get("skill_ignite_n") : 5) : 0;
            for (LivingEntity le : targets) {
                if (n++ >= maxTargets) break;
                town.sunshine.corerpg.p1.EmberCombatListener.dealP1(player, le, dmg,
                        town.sunshine.corerpg.p1.EmberSetEngine.Kind.SKILL,
                        town.sunshine.corerpg.p1.EmberCombatListener.internalTag);
                hit.add(le);
                if (look2 != null) spawnParticles(le.getLocation().add(0, 1, 0), look2.particles, 12);
            }
            if (variant && gsv != null) {
                // L08 潮蚀护符: ignite the first skill_ignite_n targets still alive (焚烬 only); L11 霜封长刀 / L14 炉芯护符: shield per target hit
                town.sunshine.corerpg.p1.EmberSetService sets = plugin.getEmberSets();
                int lit = 0;
                for (LivingEntity le : hit) {
                    if (lit >= igniteN || sets == null) break;
                    if (le.isDead() || !le.isValid()) continue;
                    if (sets.skillIgnite(player, le, gmods.get("skill_burn"))) lit++;
                }
                if (lit > 0 && gsv != null) gsv.flashSigOwned(player, "skill_ignite", "烬斩点燃 ×" + lit); // D455
                gsv.giveSkillShield(player, gmods, hit.size());
            }
        } finally {
            town.sunshine.corerpg.p1.EmberCombatListener.internalTag = prevTag;
        }
        applySlashSetIdentity(player, hit);
        maybeFlashSlashHit(player, hit.size()); // D445 烬斩命中
        if (look2 != null) {
            playSound(player.getLocation(), look2.sound);
            spawnParticles(eye.clone().add(look.clone().multiply(1.5)), look2.particles, 20);
        }
    }


    /**
     * D445 pure: flash 烬斩命中 when ≥1 valid target was damaged and debounce elapsed.
     * Multi-hit in the same window → once (caller passes total hit count).
     */
    static boolean shouldFlashSlashHit(int hitCount, long lastFlashAt, long nowMs) {
        if (hitCount < 1) return false;
        if (lastFlashAt > 0L && nowMs - lastFlashAt < SKILL_HIT_FLASH_DEBOUNCE_MS) return false;
        return true;
    }

    /**
     * D445 pure: flash 烬突落地 only when displacement finished successfully.
     * Cancelled / blocked (「前方受阻」) → false.
     */
    static boolean shouldFlashDashLand(boolean displacementOk) {
        return displacementOk;
    }

    /** D445: ActionBar 烬斩命中 — yields to set proc flash via EmberSetService. */
    private void maybeFlashSlashHit(Player player, int hitCount) {
        if (player == null) return;
        long now = System.currentTimeMillis();
        Long prev = lastSkillHitFlashAt.get(player.getUniqueId());
        long lastAt = prev == null ? 0L : prev.longValue();
        if (!shouldFlashSlashHit(hitCount, lastAt, now)) return;
        town.sunshine.corerpg.p1.EmberSetService sets = plugin.getEmberSets();
        if (sets == null) return;
        if (sets.flashSkillConfirm(player, ChatColor.GOLD + "烬斩命中")) {
            lastSkillHitFlashAt.put(player.getUniqueId(), Long.valueOf(now));
        }
    }

    /** D445: ActionBar 烬突落地 after successful dash teleport. */
    private void maybeFlashDashLand(Player player) {
        if (player == null || !shouldFlashDashLand(true)) return;
        long now = System.currentTimeMillis();
        Long prev = lastSkillHitFlashAt.get(player.getUniqueId());
        long lastAt = prev == null ? 0L : prev.longValue();
        // reuse slash debounce so slash+dash do not double-spam the bar
        if (!shouldFlashSlashHit(1, lastAt, now)) return;
        town.sunshine.corerpg.p1.EmberSetService sets = plugin.getEmberSets();
        if (sets == null) return;
        if (sets.flashSkillConfirm(player, ChatColor.AQUA + "烬突落地")) {
            lastSkillHitFlashAt.put(player.getUniqueId(), Long.valueOf(now));
        }
    }

    private String slashSetTip(Player player) {
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (ls == null) return null;
        String fam = ls.get(player).activeSet;
        if ("scorch".equals(fam)) return " · 烬刃余烬";
        if ("burst".equals(fam)) return " · 烬刃缓速";
        if ("sustain".equals(fam)) return " · 烬刃微疗";
        return null;
    }
    /**
     * D438: 烬斩套装落点 — burst Slow I 1.0s · sustain heal 0.2%H · scorch particles only (S0 ignite ❌).
     */
    private String applySlashSetIdentity(Player player, List<LivingEntity> hit) {
        town.sunshine.corerpg.p1.EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (ls == null || hit == null) return null;
        String fam = ls.get(player).activeSet;
        if (fam == null || "none".equals(fam)) return null;
        if ("scorch".equals(fam)) {
            if (!hit.isEmpty()) {
                spawnParticles(hit.get(0).getLocation().add(0, 1.0, 0), "FLAME", 8);
            }
            return " · 烬刃余烬";
        }
        if ("burst".equals(fam)) {
            if (town.sunshine.corerpg.p1.EmberMode.isP1(player) && !hit.isEmpty()) {
                int ticks = town.sunshine.corerpg.p1.EmberSkillKit.SLASH_BURST_SLOW_TICKS;
                int lim = Math.min(hit.size(), 3);
                for (int i = 0; i < lim; i++) {
                    hit.get(i).addPotionEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.SLOW, ticks, 0, false, true), true);
                }
                spawnParticles(player.getLocation().clone().add(0, 0.3, 0), "CRIT", 8);
            }
            return " · 烬刃缓速";
        }
        if ("sustain".equals(fam)) {
            if (town.sunshine.corerpg.p1.EmberMode.isP1(player)) {
                double heal = Math.max(0.25, player.getMaxHealth()
                        * town.sunshine.corerpg.p1.EmberSkillKit.SLASH_HEAL_PCT);
                player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + heal));
                spawnParticles(player.getLocation().clone().add(0, 0.5, 0), "HEART", 2);
            }
            return " · 烬刃微疗";
        }
        return null;
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
