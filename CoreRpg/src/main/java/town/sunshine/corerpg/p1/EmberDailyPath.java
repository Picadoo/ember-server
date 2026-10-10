package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

/**
 * D514: daily clear-bounty chase path — light / full / flex.
 * NEW acquisition: sticky target for p2_bounty (1 vs 3 clears). Settle chase + enter glance.
 * Distinct from ShortPath (sx resolve), BountyPath (variety ensure), FeePath (abyss fee).
 * Chat only. Zero reward table / AFK / sx change. Not combo sticky, not forge/enter/ActionBar cue.
 */
public final class EmberDailyPath {

    public static final String C_PATH = "p1_daily_path";
    public static final String C_OFFER = "p1_daily_path_offer";

    public static final int NONE = 0;
    /** Chase first daily tier (clears=1) */
    public static final int LIGHT = 1;
    /** Chase top daily tier (clears=3) */
    public static final int FULL = 2;
    /** No sticky target — stock bounty line only */
    public static final int FLEX = 3;

    private EmberDailyPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == LIGHT || id == FULL || id == FLEX;
    }

    public static boolean set(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_PATH, "all");
        if (cur == id) return false;
        d.addPeriodCount(C_PATH, "all", id - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "取消".equals(s)) return NONE;
        if ("light".equals(s) || "轻委".equals(s) || "轻".equals(s) || "1".equals(s)) return LIGHT;
        if ("full".equals(s) || "满委".equals(s) || "满".equals(s) || "grind".equals(s) || "2".equals(s)) return FULL;
        if ("flex".equals(s) || "随意".equals(s) || "off".equals(s) || "3".equals(s)) return FLEX;
        return -1;
    }

    public static String key(int id) {
        if (id == LIGHT) return "light";
        if (id == FULL) return "full";
        if (id == FLEX) return "flex";
        return "none";
    }

    public static String label(int id) {
        if (id == LIGHT) return "日委·轻委";
        if (id == FULL) return "日委·满委";
        if (id == FLEX) return "日委·随意";
        return "未选";
    }

    public static String tip(int id) {
        if (id == LIGHT) return "今日以首档委托为目标（通关 1 局）";
        if (id == FULL) return "今日追满档委托（通关 3 局）";
        if (id == FLEX) return "不设日委目标，只看默认委托行";
        return "点选每日通关委托追猎（改节奏 · 不改奖励表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8日委路径：首通 Q01 后可选";
        if (!valid(id)) return "§8日委路径：未选 · /corerpg p1 dailypath";
        return "§e日委路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Target clears for path; 0 = no sticky chase. Bukkit-free. */
    public static int targetClears(int path, List<EmberRunRules.BountyTier> tiers) {
        if (tiers == null || tiers.isEmpty()) return 0;
        if (path == LIGHT) return tiers.get(0).clears;
        if (path == FULL) return tiers.get(tiers.size() - 1).clears;
        return 0;
    }

    /** Path chase line after settle, or null when flex/none. Bukkit-free. */
    public static String chaseLine(int path, List<EmberRunRules.BountyTier> tiers, int done) {
        int t = targetClears(path, tiers);
        if (t <= 0) return null;
        if (done >= t) return "§a日委路径达标 ✔ §7（" + label(path) + " · " + done + "/" + t + "）";
        return "§e日委路径 §f" + done + "/" + t + " §7· 再通关 " + (t - done) + " 局（" + label(path) + "）";
    }

    /** Enter glance when behind sticky target. */
    public static void maybeGlanceBeforeRun(Player p, EmberRunService runs) {
        if (p == null || runs == null) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (d == null || !runs.progressFlag(d, "q01")) return;
        int path = get(d);
        if (!valid(path) || path == FLEX) return;
        List<EmberRunRules.BountyTier> tiers = runs.bountyTiers();
        int done = d.periodCount(EmberRunService.C_BOUNTY, DailyService.today());
        String line = chaseLine(path, tiers, done);
        if (line != null) p.sendMessage(EmberRunService.P + line);
    }

    /** Settle hook: path chase after stock daily bounty line. */
    public static void tellAfterSettle(Player p, PlayerData d, EmberRunService runs,
                                       List<EmberRunRules.BountyTier> tiers, int bountyN) {
        if (p == null || d == null || runs == null) return;
        String line = chaseLine(get(d), tiers, bountyN);
        if (line != null) p.sendMessage(EmberRunService.P + line);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选每日委托追猎（改节奏 · 可随时改 · 不改奖励表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[轻委]", "/corerpg p1 dailypath light", tip(LIGHT), "GREEN"},
                new String[]{"[满委]", "/corerpg p1 dailypath full", tip(FULL), "GOLD"},
                new String[]{"[随意]", "/corerpg p1 dailypath flex", tip(FLEX), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 dailypath clear", "清空偏好", "DARK_GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        flush(p);
        offerPick(p);
        return true;
    }

    public static void scheduleOfferAfterQ01(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 270L); // after bar ~255
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        EmberRunService runs = pl.getEmberRuns();
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!runs.progressFlag(d, "q01")) {
            p.sendMessage(EmberRunService.P + "§c日委路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空日委路径（无 sticky 目标）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a日委 → §f" + label(id) + " §7· " + tip(id));
        List<EmberRunRules.BountyTier> tiers = runs.bountyTiers();
        int done = d.periodCount(EmberRunService.C_BOUNTY, DailyService.today());
        String chase = chaseLine(id, tiers, done);
        if (chase != null) p.sendMessage(P + chase);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/ember_p1_adventure", "去通关计日委", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 dailypath", "重选", "GRAY"});
    }

    private static PlayerData dataOf(Player p) {
        CoreRpgPlugin pl = plugin();
        return pl == null || p == null ? null : pl.getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl != null && p != null) pl.getDataStore().flushMutation(p.getUniqueId());
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}
