package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D507: challenge/abyss fail-stamina refund path — keep / light / skip.
 * NEW acquisition: clamps D128 first-fail stamina refund share (never raises table).
 * Distinct from D505 death-potion refund. Chat only. Zero fail_refund.yml rewrite.
 * Not combo sticky, not roll bias, not prep/short/twist/room twin, not forge/enter cue.
 */
public final class EmberFailPath {

    public static final String C_PATH = "p1_fail_path";
    public static final String C_OFFER = "p1_fail_path_offer";

    public static final int NONE = 0;
    /** Table fail_refund share (0.5) */
    public static final int KEEP = 1;
    /** Half of table share */
    public static final int LIGHT = 2;
    /** No stamina refund (still consumes once-per-day latch) */
    public static final int SKIP = 3;

    /** failRefund return: intentional skip latch written, 0 stamina */
    public static final int RESULT_SKIP = -2;

    private EmberFailPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == KEEP || id == LIGHT || id == SKIP;
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
        if ("keep".equals(s) || "full".equals(s) || "满退".equals(s) || "1".equals(s)) return KEEP;
        if ("light".equals(s) || "轻退".equals(s) || "2".equals(s)) return LIGHT;
        if ("skip".equals(s) || "bare".equals(s) || "不退".equals(s) || "3".equals(s)) return SKIP;
        return -1;
    }

    public static String key(int id) {
        if (id == KEEP) return "keep";
        if (id == LIGHT) return "light";
        if (id == SKIP) return "skip";
        return "none";
    }

    public static String label(int id) {
        if (id == KEEP) return "败退·满退";
        if (id == LIGHT) return "败退·轻退";
        if (id == SKIP) return "败退·不退";
        return "未选";
    }

    public static String tip(int id) {
        if (id == KEEP) return "挑战/深渊今日首次失败：按表退一半体力";
        if (id == LIGHT) return "挑战/深渊今日首次失败：只退表比例的一半体力";
        if (id == SKIP) return "挑战/深渊今日首次失败：不退体力（仍占今日一次）";
        return "点选失败退体偏好（改获取 · 不抬表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8败退路径：首通 Q07 后可选（挑战/深渊）";
        if (!valid(id)) return "§8败退路径：未选 · /corerpg p1 failpath";
        return "§e败退路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Never raises above table share. KEEP/NONE → table; LIGHT → table/2; SKIP → 0. */
    public static double effectiveShare(int path, double tableShare) {
        double t = Math.max(0.0, Math.min(1.0, tableShare));
        if (path == SKIP) return 0.0;
        if (path == LIGHT) return t * 0.5;
        return t;
    }

    public static String skipSuffix() {
        return " §7· 败退路径·不退：不退体力，已记今日一次";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选失败退体（挑战/深渊今日首次失败 · 可随时改 · 不抬表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[满退]", "/corerpg p1 failpath keep", tip(KEEP), "GREEN"},
                new String[]{"[轻退]", "/corerpg p1 failpath light", tip(LIGHT), "GOLD"},
                new String[]{"[不退]", "/corerpg p1 failpath skip", tip(SKIP), "GRAY"},
                new String[]{"[取消]", "/corerpg p1 failpath clear", "清空偏好", "DARK_GRAY"});
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

    public static void scheduleOfferAfterQ07(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 140L);
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
        if (!runs.progressFlag(d, "q07")) {
            p.sendMessage(EmberRunService.P + "§c败退路径需本人首通 Q07（挑战/深渊才有失败退体）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空败退路径（按表退体）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        double table = runs.maps() == null ? 0.5 : runs.maps().failRefund;
        double eff = effectiveShare(id, table);
        int sample = EmberRunRules.failRefundAmount(runs.maps() == null ? 30 : runs.maps().cost, eff);
        p.sendMessage(P + "§a败退 → §f" + label(id) + " §7· " + tip(id)
                + " §8（表 " + EmberSettleService.failRefundPct(table) + "% → 你 "
                + EmberSettleService.failRefundPct(eff) + "% · 例退 " + sample + " 体力）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "挑战/深渊失败时按偏好退体", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 failpath", "重选", "GRAY"});
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
