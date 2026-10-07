package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 1.5.0 (2026-09-26): second vanilla-level source + season-pass XP. Config: progress.yml.
 *  - /corerpg xpreward <player> <elite|boss>  (console/admin; MM onDeath) → vanilla levels, daily cap
 *  - /corerpg passxp <player> <source>          (console/admin; DP clear rewards) → pass XP, daily cap
 *  - sign-in grants pass XP internally.
 */
public final class ProgressService {

    private final CoreRpgPlugin plugin;
    private final PlayerDataStore dataStore;

    private boolean killLevelsEnabled = true;
    private final Map<String, Integer> killLevels = new LinkedHashMap<String, Integer>();
    private int killLevelsDailyCap = 6;

    private boolean passEnabled = true;
    private final Map<String, Integer> passSources = new LinkedHashMap<String, Integer>();
    private int passDailyCap = 100;
    private int passXpPerLevel = 100;
    private int passMaxLevel = 30;

    // ---- 1.6.0 ember level ----
    private boolean emberEnabled = true;
    private final Map<String, Integer> emberSources = new LinkedHashMap<String, Integer>();
    private int emberKillDailyCap = 100;
    private int emberCombatDailyCap = 150;
    private int emberBase = 60;
    private int emberStep = 12;
    private int emberStepFrom = 10;
    private int emberMaxLevel = 60;

    // ---- 1.6.0 pass rewards / season ----
    private final Map<Integer, Map<String, Integer>> passFree = new java.util.TreeMap<Integer, Map<String, Integer>>();
    private final Map<Integer, Map<String, Integer>> passPaid = new java.util.TreeMap<Integer, Map<String, Integer>>();
    private String seasonId = "S1";

    // ---- 1.6.0 vip tiers ----
    private final java.util.List<Integer> vipThresholds = new java.util.ArrayList<Integer>();
    private int vipXpBonusPerTier = 1;
    private int vipWarehouseSlotsPerTier = 1;

    // ---- 1.7.0 level gates ----
    private final Map<String, Integer> levelGates = new LinkedHashMap<String, Integer>();

    public int gateLevel(String id) {
        Integer v = levelGates.get(id == null ? "" : id.toLowerCase());
        return v == null ? 0 : v;
    }

    public boolean passesGate(PlayerData d, String id) {
        return d.getEmberLevel() >= gateLevel(id);
    }

    public String gateRefusal(PlayerData d, String id, String label) {
        return ChatColor.RED + "[余烬] " + label + " 需要余烬等级 " + ChatColor.YELLOW + "Lv." + gateLevel(id)
                + ChatColor.RED + "（当前 Lv." + d.getEmberLevel() + "）" + ChatColor.GRAY + " · /corerpg level 查看升级进度";
    }

    public ProgressService(CoreRpgPlugin plugin, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "progress.yml");
        if (!file.exists()) {
            plugin.saveResource("progress.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        killLevelsEnabled = cfg.getBoolean("kill_levels.enabled", true);
        killLevelsDailyCap = Math.max(0, cfg.getInt("kill_levels.daily_cap", 6));
        killLevels.clear();
        ConfigurationSection kl = cfg.getConfigurationSection("kill_levels.levels");
        if (kl != null) {
            for (String k : kl.getKeys(false)) killLevels.put(k.toLowerCase(), Math.max(0, kl.getInt(k)));
        } else {
            killLevels.put("elite", 1);
            killLevels.put("boss", 2);
        }
        passEnabled = cfg.getBoolean("pass_xp.enabled", true);
        passDailyCap = Math.max(0, cfg.getInt("pass_xp.daily_cap", 100));
        passXpPerLevel = Math.max(1, cfg.getInt("pass_xp.xp_per_level", 100));
        passMaxLevel = Math.max(1, cfg.getInt("pass_xp.max_level", 30));
        passSources.clear();
        ConfigurationSection ps = cfg.getConfigurationSection("pass_xp.sources");
        if (ps != null) {
            for (String k : ps.getKeys(false)) passSources.put(k.toLowerCase(), Math.max(0, ps.getInt(k)));
        } else {
            passSources.put("sign", 10);
            passSources.put("daily_clear", 20);
            passSources.put("weekly_clear", 40);
        }
        emberEnabled = cfg.getBoolean("ember_xp.enabled", true);
        emberKillDailyCap = Math.max(0, cfg.getInt("ember_xp.kill_daily_cap", 100));
        emberCombatDailyCap = Math.max(0, cfg.getInt("ember_xp.combat_daily_cap", 150));
        emberBase = Math.max(1, cfg.getInt("ember_xp.curve.base", 60));
        emberStep = Math.max(0, cfg.getInt("ember_xp.curve.step", 12));
        emberStepFrom = cfg.getInt("ember_xp.curve.step_from", 10);
        emberMaxLevel = Math.max(1, cfg.getInt("ember_xp.max_level", 60));
        emberSources.clear();
        ConfigurationSection es = cfg.getConfigurationSection("ember_xp.sources");
        if (es != null) for (String k : es.getKeys(false)) emberSources.put(k.toLowerCase(), Math.max(0, es.getInt(k)));
        passFree.clear();
        passPaid.clear();
        loadTrack(cfg.getConfigurationSection("pass_rewards.free"), passFree);
        loadTrack(cfg.getConfigurationSection("pass_rewards.paid"), passPaid);
        vipThresholds.clear();
        for (Object o : cfg.getList("vip_tiers.thresholds", new java.util.ArrayList<Object>())) {
            try { vipThresholds.add(Integer.parseInt(String.valueOf(o))); } catch (NumberFormatException ignored) {}
        }
        vipXpBonusPerTier = Math.max(0, cfg.getInt("vip_tiers.ember_xp_bonus_percent_per_tier", 1));
        vipWarehouseSlotsPerTier = Math.max(0, cfg.getInt("vip_tiers.warehouse_slots_per_tier", 1));
        levelGates.clear();
        ConfigurationSection lg = cfg.getConfigurationSection("level_gates");
        if (lg != null) {
            for (String k : lg.getKeys(false)) levelGates.put(k.toLowerCase(), Math.max(0, lg.getInt(k)));
        } else {
            levelGates.put("daily", 10); levelGates.put("weekly", 20); levelGates.put("abyss", 25);
            levelGates.put("calamity", 30); levelGates.put("raid", 35); levelGates.put("guild_boss", 0); levelGates.put("elite", 40);
        }
        File sf = new File(plugin.getDataFolder(), "season.yml");
        YamlConfiguration sy = YamlConfiguration.loadConfiguration(sf);
        seasonId = sy.getString("current_season_id", "S1");
        if (!sf.exists()) saveSeason();
    }

    private void loadTrack(ConfigurationSection sec, Map<Integer, Map<String, Integer>> out) {
        if (sec == null) return;
        for (String lv : sec.getKeys(false)) {
            ConfigurationSection r = sec.getConfigurationSection(lv);
            if (r == null) continue;
            Map<String, Integer> m = new LinkedHashMap<String, Integer>();
            for (String k : r.getKeys(false)) if (r.getInt(k) > 0) m.put(k, r.getInt(k));
            try { out.put(Integer.parseInt(lv), m); } catch (NumberFormatException ignored) {}
        }
    }

    private void saveSeason() {
        File sf = new File(plugin.getDataFolder(), "season.yml");
        YamlConfiguration sy = new YamlConfiguration();
        sy.set("current_season_id", seasonId);
        try { sy.save(sf); } catch (java.io.IOException e) { plugin.getLogger().warning("season.yml save failed: " + e.getMessage()); }
    }

    public String getSeasonId() { return seasonId; }

    // ---------------- ember level ----------------

    public int xpToNext(int level) {
        return emberBase + emberStep * Math.max(0, level - emberStepFrom);
    }

    /** @return ember XP actually granted (after caps + VIP bonus). */
    public int grantEmberXp(Player p, String source) {
        if (!emberEnabled || p == null) return 0;
        String key = source == null ? "" : source.toLowerCase();
        if (plugin.legacyXpBlocked(key)) return 0; // D200 S0-4 ①: every grantEmberXp(source) caller is a pre-P1 source; P1 pays via grantFlatEmberXp
        Integer base = emberSources.get(key);
        if (base == null || base <= 0) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        if (!today.equals(d.getEmberXpDate())) {
            d.setEmberXpDate(today);
            d.setEmberXpKillToday(0);
            d.setEmberXpCombatToday(0);
        }
        int give = base;
        if ("kill".equals(key)) {
            give = Math.min(give, Math.max(0, emberKillDailyCap - d.getEmberXpKillToday()));
            if (give <= 0) return 0;
            d.setEmberXpKillToday(d.getEmberXpKillToday() + give);
        } else if ("elite".equals(key) || "boss".equals(key)) {
            give = Math.min(give, Math.max(0, emberCombatDailyCap - d.getEmberXpCombatToday()));
            if (give <= 0) return 0;
            d.setEmberXpCombatToday(d.getEmberXpCombatToday() + give);
        }
        int tier = d.getVipTier();
        if (tier > 0 && vipXpBonusPerTier > 0) give += (give * tier * vipXpBonusPerTier + 50) / 100;
        return applyEmberXp(p, d, give, !"kill".equals(key), null) ? give : 0;
    }

    /** 1.8.0: flat ember XP (mainline quest) — no daily caps, no VIP bonus. */
    public int grantFlatEmberXp(Player p, int amount, String label) {
        if (!emberEnabled || p == null || amount <= 0) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        return applyEmberXp(p, d, amount, true, label) ? amount : 0;
    }

    private boolean applyEmberXp(Player p, PlayerData d, int give, boolean announce, String label) {
        int level = d.getEmberLevel();
        final int fromLevel = level;
        if (level >= emberMaxLevel) {
            dataStore.flushMutation(p.getUniqueId());
            return false;
        }
        int xp = d.getEmberXp() + give;
        int ups = 0;
        int talentGain = 0;
        TalentService ts = plugin.getTalentServicePublic();
        int earnedBefore = d.getTalentPointsEarned();
        while (level < emberMaxLevel && xp >= xpToNext(level)) {
            xp -= xpToNext(level);
            level++;
            ups++;
        }
        if (level >= emberMaxLevel) xp = 0;
        d.setEmberXp(xp);
        if (ups > 0) {
            d.setEmberLevel(level);
            // Formula A via TalentService: L<=30 legacy; L>30 = 20 + floor((L-30)/3), no double-add
            if (ts != null) {
                int want = ts.computeStartingEarned(level);
                talentGain = Math.max(0, want - earnedBefore);
                if (talentGain > 0) {
                    talentGain = Math.min(talentGain, Math.max(0, ts.getMaxSpendablePoints() - earnedBefore));
                }
            }
            if (talentGain > 0) d.setTalentPointsEarned(earnedBefore + talentGain);
        }
        dataStore.flushMutation(p.getUniqueId());
        if (announce) {
            p.sendMessage(ChatColor.YELLOW + (label != null ? label + " " : "[余烬] ") + "等级经验 +" + give + ChatColor.GRAY + "（Lv." + level + " · "
                    + xp + "/" + (level >= emberMaxLevel ? "MAX" : String.valueOf(xpToNext(level))) + "）");
        }
        if (ups > 0) {
            p.sendMessage(ChatColor.GOLD + "[余烬] 升级！余烬等级 Lv." + level
                    + (talentGain > 0 ? ChatColor.GREEN + " · 天赋点 +" + talentGain : ""));
        }
        if (ups > 0) {
            QuestService qs = plugin.getQuestService();
            if (qs != null) qs.onLevelChanged(p);
            AfkTierService afk = plugin.getAfkTierService();
            if (afk != null) afk.onLevelUp(p, fromLevel, level);
        }
        return true;
    }

    public void cmdLevel(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        int level = d.getEmberLevel();
        p.sendMessage(ChatColor.GOLD + "[余烬] 等级 Lv." + level + ChatColor.GRAY + " · 经验 " + d.getEmberXp() + "/"
                + (level >= emberMaxLevel ? "MAX" : String.valueOf(xpToNext(level))) + " · 上限 Lv." + emberMaxLevel);
        boolean today = DailyService.today().equals(d.getEmberXpDate());
        p.sendMessage(ChatColor.GRAY + "  今日：击杀经验 " + (today ? d.getEmberXpKillToday() : 0) + "/" + emberKillDailyCap
                + " · 精英/首领经验 " + (today ? d.getEmberXpCombatToday() : 0) + "/" + emberCombatDailyCap
                + " · 天赋点 可用 " + d.getTalentPointsAvailable());
        p.sendMessage(ChatColor.DARK_GRAY + "  来源：签到 / 日·周·深渊·团本·盟Boss 通关 / 精英·首领 / 击杀（日上限）");
    }

    // ---------------- pass season / rewards ----------------

    /** Lazy season roll-over: a player's pass progress belongs to one season id. */
    public void ensureSeason(PlayerData d) {
        if (seasonId.equals(d.getPassSeasonId())) return;
        boolean fresh = d.getPassSeasonId().isEmpty();
        d.setPassSeasonId(seasonId);
        if (!fresh) {
            d.setPassXp(0);
            d.setPassXpToday(0);
            d.setPassFreeClaimedLevel(0);
            d.setPassPaidClaimedLevel(0);
            d.setSeasonPassPaid(false);
        }
    }

    public void cmdPassClaim(Player p) {
        if (CashCoinRules.p1BlocksCoin(town.sunshine.corerpg.p1.EmberMode.active())) {
            p.sendMessage(ChatColor.GRAY + "[战令] P1 模式下不发旧战令等级奖励（请用主线 / 签到）。");
            return;
        }
        PlayerData d = dataStore.get(p.getUniqueId());
        ensureSeason(d);
        int lv = passLevel(d);
        MailService mail = plugin.getMailService();
        if (mail == null) { p.sendMessage(ChatColor.RED + "[战令] 邮寄服务未就绪"); return; }
        int sent = 0;
        int from = d.getPassFreeClaimedLevel();
        for (int l = from + 1; l <= lv; l++) {
            Map<String, Integer> r = passFree.get(l);
            if (r != null && !r.isEmpty()) {
                if (!mail.deliverCustom(p.getUniqueId(), "pass_free_" + seasonId + "_" + l, "战令·免费轨 Lv." + l,
                        "赛季 " + seasonId + " 免费轨等级奖励", r)) break;
                sent++;
            }
            d.setPassFreeClaimedLevel(l);
        }
        if (d.isSeasonPassPaid()) {
            for (int l = d.getPassPaidClaimedLevel() + 1; l <= lv; l++) {
                Map<String, Integer> r = passPaid.get(l);
                if (r != null && !r.isEmpty()) {
                    if (!mail.deliverCustom(p.getUniqueId(), "pass_paid_" + seasonId + "_" + l, "战令·付费轨 Lv." + l,
                            "赛季 " + seasonId + " 付费轨等级奖励", r)) break;
                    sent++;
                }
                d.setPassPaidClaimedLevel(l);
            }
        }
        dataStore.flushMutation(p.getUniqueId());
        if (sent == 0) {
            p.sendMessage(ChatColor.GRAY + "[战令] 没有可领取的等级奖励（Lv." + lv + "）");
        } else {
            p.sendMessage(ChatColor.GREEN + "[战令] 已寄出 " + sent + " 封等级奖励邮件 · /corerpg mail claim all");
        }
    }

    public void cmdPassRewards(Player p) {
        PlayerData d = dataStore.get(p.getUniqueId());
        ensureSeason(d);
        int lv = passLevel(d);
        p.sendMessage(ChatColor.AQUA + "[战令] 赛季 " + seasonId + " · Lv." + lv + " · 免费轨已领至 " + d.getPassFreeClaimedLevel()
                + " · 付费轨" + (d.isSeasonPassPaid() ? "已领至 " + d.getPassPaidClaimedLevel() : "未开通"));
        for (int l = Math.max(1, lv); l <= Math.min(passMaxLevel, lv + 2); l++) {
            p.sendMessage(ChatColor.GRAY + "  Lv." + l + " 免费 " + passFree.getOrDefault(l, new LinkedHashMap<String, Integer>())
                    + " · 付费 " + passPaid.getOrDefault(l, new LinkedHashMap<String, Integer>()));
        }
    }

    /** /corerpg pass season [reset <newId>] (admin for reset). */
    public void cmdSeason(CommandSender sender, String[] args) {
        if (args.length >= 4 && "reset".equalsIgnoreCase(args[2])) {
            if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return; }
            String next = args[3];
            if (next.equals(seasonId)) { sender.sendMessage(ChatColor.RED + "新赛季 ID 与当前相同：" + seasonId); return; }
            String old = seasonId;
            seasonId = next;
            saveSeason();
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData d = dataStore.get(p.getUniqueId());
                ensureSeason(d);
                dataStore.flushMutation(p.getUniqueId());
                p.sendMessage(ChatColor.GOLD + "[战令] 新赛季 " + seasonId + " 开始！战令经验与领取进度已重置。");
            }
            sender.sendMessage("[CoreRpg] 赛季 " + old + " → " + seasonId + "（离线玩家下次访问时重置）");
            return;
        }
        sender.sendMessage("[战令] 当前赛季：" + seasonId + (sender.hasPermission("corerpg.admin") ? " · /corerpg pass season reset <新ID>" : ""));
    }

    // ---------------- VIP tiers ----------------

    public int tierFor(int toppedUp) {
        int t = 0;
        for (int i = 0; i < vipThresholds.size(); i++) if (toppedUp >= vipThresholds.get(i)) t = i + 1;
        return t;
    }

    public int nextThreshold(int tier) {
        return tier < vipThresholds.size() ? vipThresholds.get(tier) : -1;
    }

    /** Record a top-up (positive crystal grant) and apply tier-ups + one-time perks. */
    public void recordTopUp(Player p, int amount) {
        if (p == null || amount <= 0) return;
        PlayerData d = dataStore.get(p.getUniqueId());
        d.setVipToppedUp(d.getVipToppedUp() + amount);
        int before = d.getVipTier();
        int after = Math.max(before, tierFor(d.getVipToppedUp()));
        if (after > before) {
            d.setVipTier(after);
            WarehouseService wh = plugin.getWarehouseServicePublic();
            int slots = (after - before) * vipWarehouseSlotsPerTier;
            if (wh != null && slots > 0) {
                wh.ensureDefaults(d);
                d.setWarehouseSlotsUnlocked(Math.min(wh.getMaxSlots(), d.getWarehouseSlotsUnlocked() + slots));
            }
            p.sendMessage(ChatColor.LIGHT_PURPLE + "[勋阶] 晋升 " + tierName(after) + ChatColor.GRAY
                    + "（仓库 +" + slots + " 格 · 余烬经验 +" + (after * vipXpBonusPerTier) + "%）");
        }
        dataStore.flushMutation(p.getUniqueId());
    }

    public String tierName(int tier) {
        String[] roman = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return tier <= 0 ? "无" : ("勋阶" + (tier < roman.length ? roman[tier] : String.valueOf(tier)));
    }

    public int getVipXpBonusPerTier() { return vipXpBonusPerTier; }


    // ---------------- kill levels ----------------

    /** @return levels actually granted (after cap). */
    public int grantKillLevels(Player p, String kind) {
        if (!killLevelsEnabled || p == null) return 0;
        Integer want = killLevels.get(kind == null ? "" : kind.toLowerCase());
        if (want == null || want <= 0) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        String today = DailyService.today();
        if (!today.equals(d.getKillLevelsDate())) {
            d.setKillLevelsDate(today);
            d.setKillLevelsToday(0);
        }
        int left = killLevelsDailyCap - d.getKillLevelsToday();
        int give = Math.min(want, Math.max(0, left));
        if (give <= 0) {
            dataStore.flushMutation(p.getUniqueId());
            return 0;
        }
        d.setKillLevelsToday(d.getKillLevelsToday() + give);
        dataStore.flushMutation(p.getUniqueId());
        p.giveExpLevels(give);
        p.sendMessage(ChatColor.GREEN + "[余烬] 击杀" + ("boss".equalsIgnoreCase(kind) ? "首领" : "精英")
                + " · 经验等级 +" + give + ChatColor.GRAY + "（今日 " + d.getKillLevelsToday() + "/" + killLevelsDailyCap + "）");
        return give;
    }

    // ---------------- pass xp ----------------

    public int passLevel(PlayerData d) {
        return Math.min(passMaxLevel, d.getPassXp() / passXpPerLevel);
    }

    /** @return xp actually granted (after cap). */
    public int grantPassXp(Player p, String source) {
        if (!passEnabled || p == null) return 0;
        if (plugin.legacyXpBlocked(source)) return 0; // D200 S0-4 ①: legacy pass XP closed while P1 is on (pass claim already refused, D199)
        Integer want = passSources.get(source == null ? "" : source.toLowerCase());
        if (want == null || want <= 0) return 0;
        PlayerData d = dataStore.get(p.getUniqueId());
        ensureSeason(d);
        String today = DailyService.today();
        if (!today.equals(d.getPassXpDate())) {
            d.setPassXpDate(today);
            d.setPassXpToday(0);
        }
        int give = Math.min(want, Math.max(0, passDailyCap - d.getPassXpToday()));
        if (give <= 0) {
            p.sendMessage(ChatColor.GRAY + "[战令] 今日战令经验已达上限 " + passDailyCap);
            dataStore.flushMutation(p.getUniqueId());
            return 0;
        }
        int before = passLevel(d);
        d.setPassXpToday(d.getPassXpToday() + give);
        d.setPassXp(d.getPassXp() + give);
        dataStore.flushMutation(p.getUniqueId());
        int after = passLevel(d);
        p.sendMessage(ChatColor.AQUA + "[战令] 经验 +" + give + ChatColor.GRAY + "（Lv." + after + " · "
                + (d.getPassXp() % passXpPerLevel) + "/" + passXpPerLevel + " · 今日 " + d.getPassXpToday() + "/" + passDailyCap + "）");
        if (after > before) {
            p.sendMessage(ChatColor.GOLD + "[战令] 升级！赛季等级 Lv." + after + ChatColor.GRAY + " · /corerpg pass claim 领取等级奖励");
        }
        return give;
    }

    public String passLine(PlayerData d) {
        ensureSeason(d);
        return "赛季 " + seasonId + " · 等级 Lv." + passLevel(d) + " · 经验 " + (d.getPassXp() % passXpPerLevel) + "/" + passXpPerLevel
                + " · 今日 " + (DailyService.today().equals(d.getPassXpDate()) ? d.getPassXpToday() : 0) + "/" + passDailyCap;
    }

    // ---------------- commands ----------------

    /** /corerpg progress <player> <source> — clear rewards: pass XP + ember XP from one console call. */
    public boolean cmdProgress(CommandSender sender, String[] args) {
        if (!sender.hasPermission("corerpg.admin")) { sender.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
        if (args.length < 3) { sender.sendMessage("/corerpg progress <player> <source>"); return true; }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) { sender.sendMessage(ChatColor.RED + "玩家不在线：" + args[1]); return true; }
        if (town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(target)) { // G04 E02/E11: the run settlement already pays 120 xp
            sender.sendMessage("[CoreRpg] progress " + target.getName() + " " + args[2] + " → skipped (P1 main-map run)");
            return true;
        }
        String src = args[2] == null ? "" : args[2].toLowerCase();
        // Stage 4.4: elite_weekly once per ISO week (Asia/Shanghai) — mark + skip double XP
        // Stage 4.5: also fire quest elite_weekly_clear (dual-trigger with DP COMPLETE script)
        boolean eliteAlready = false;
        if ("elite_weekly".equals(src)) {
            PlayerData d = dataStore.get(target.getUniqueId());
            String week = DailyService.weekId();
            String mark = EliteService.CLEAR_MARK + "=" + week;
            if (d.getLootWeekMarks().contains(mark)) {
                eliteAlready = true;
                sender.sendMessage("[CoreRpg] progress " + target.getName() + " elite_weekly → already marked " + week);
            } else {
                d.addLootWeekMark(EliteService.CLEAR_MARK, week);
                dataStore.flushMutation(target.getUniqueId());
            }
        }
        QuestService qs = plugin.getQuestService();
        if ("elite_weekly".equals(src)) {
            // Always notify quest (even on re-mark) so mainline is not stuck if DP quest line missed
            if (qs != null) {
                qs.onEvent(target, "elite_weekly_clear");
                qs.checkPassive(target);
            }
            if (eliteAlready) return true; // skip double XP
        }
        int px = grantPassXp(target, args[2]);
        int ex = grantEmberXp(target, args[2]);
        if (qs != null) qs.onEvent(target, args[2]);
        if (!(sender instanceof Player) || sender != target) {
            sender.sendMessage("[CoreRpg] progress " + target.getName() + " " + args[2] + " → pass +" + px + " · ember +" + ex);
        }
        return true;
    }

    /** /corerpg xpreward <player> <elite|boss> · /corerpg passxp <player> <source> */
    public boolean cmdAdminGrant(CommandSender sender, String[] args, boolean pass) {
        if (!sender.hasPermission("corerpg.admin")) {
            sender.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(pass ? "/corerpg passxp <player> <" + String.join("|", passSources.keySet()) + ">"
                    : "/corerpg xpreward <player> <" + String.join("|", killLevels.keySet()) + ">");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线：" + args[1]);
            return true;
        }
        if (town.sunshine.corerpg.p1.EmberRunService.blocksLegacy(target)) return true; // G04 E02
        int got = pass ? grantPassXp(target, args[2]) : grantKillLevels(target, args[2]);
        if (!pass) grantEmberXp(target, args[2]);
        if (!(sender instanceof Player) || sender != target) {
            sender.sendMessage("[CoreRpg] " + (pass ? "passxp " : "xpreward ") + target.getName() + " " + args[2] + " → +" + got);
        }
        return true;
    }
}
