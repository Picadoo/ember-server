package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D510: rush-chain break timing path — full / short / snap.
 * NEW play: clamps party-leader between-boss rest seconds (never raises table).
 * Distinct from D509 rest heal. Chat only. Zero break_secs yml rewrite / AFK / sx.
 * Not combo sticky, not refund clamp twin, not roll bias, not forge/enter/ActionBar cue.
 */
public final class EmberBreakPath {

    public static final String C_PATH = "p1_break_path";
    public static final String C_OFFER = "p1_break_path_offer";

    public static final int NONE = 0;
    /** Table rushBreak */
    public static final int FULL = 1;
    /** Half of table */
    public static final int SHORT = 2;
    /** Quarter of table, floored at 2s */
    public static final int SNAP = 3;

    public static final double SNAP_FLOOR = 2.0;

    private EmberBreakPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FULL || id == SHORT || id == SNAP;
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
        if ("full".equals(s) || "满歇".equals(s) || "1".equals(s)) return FULL;
        if ("short".equals(s) || "短歇".equals(s) || "2".equals(s)) return SHORT;
        if ("snap".equals(s) || "急歇".equals(s) || "3".equals(s)) return SNAP;
        return -1;
    }

    public static String key(int id) {
        if (id == FULL) return "full";
        if (id == SHORT) return "short";
        if (id == SNAP) return "snap";
        return "none";
    }

    public static String label(int id) {
        if (id == FULL) return "间歇·满歇";
        if (id == SHORT) return "间歇·短歇";
        if (id == SNAP) return "间歇·急歇";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FULL) return "连战/前哨阶段间按表休息秒数";
        if (id == SHORT) return "连战/前哨阶段间休息缩短为表的一半";
        if (id == SNAP) return "连战/前哨阶段间急歇（表的 1/4，最少 2 秒）";
        return "点选连战间歇时长（改节奏 · 不抬表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8间歇路径：首通 Q04 后可选（连战/前哨）";
        if (!valid(id)) return "§8间歇路径：未选 · /corerpg p1 breakpath";
        return "§e间歇路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Never raises above table. FULL/NONE → table; SHORT → table/2; SNAP → max(2, table/4). */
    public static double effectiveBreak(int path, double tableSecs) {
        double t = Math.max(0.0, tableSecs);
        if (path == SNAP) return Math.min(t, Math.max(SNAP_FLOOR, t * 0.25));
        if (path == SHORT) return t * 0.5;
        return t;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选连战间歇（阶段间休息秒数 · 可随时改 · 不抬表；残响单首领无阶段）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[满歇]", "/corerpg p1 breakpath full", tip(FULL), "GREEN"},
                new String[]{"[短歇]", "/corerpg p1 breakpath short", tip(SHORT), "GOLD"},
                new String[]{"[急歇]", "/corerpg p1 breakpath snap", tip(SNAP), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 breakpath clear", "清空偏好", "DARK_GRAY"});
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

    public static void scheduleOfferAfterQ04(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 120L); // after rest ~100L
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
        if (!runs.progressFlag(d, "q04")) {
            p.sendMessage(EmberRunService.P + "§c间歇路径需本人首通 Q04（连战/前哨才有阶段间歇）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空间歇路径（按表休息）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        double table = 10.0;
        double eff = effectiveBreak(id, table);
        p.sendMessage(P + "§a间歇 → §f" + label(id) + " §7· " + tip(id)
                + " §8（例表 " + Math.round(table) + "s → 你 " + Math.round(eff) + "s · 队长路径）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "连战/前哨阶段间按偏好休息", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 breakpath", "重选", "GRAY"});
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
