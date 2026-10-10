package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D509: rush-chain rest heal path — full / light / bare.
 * NEW play: clamps per-player heal share between rush/outpost chain bosses (never raises table).
 * Echo single-boss unaffected. Chat only. Zero rushHeal yml rewrite / AFK / sx / forge.
 * Not combo sticky, not refund/fail twin (potion/stamina), not roll bias, not enter/ActionBar cue.
 */
public final class EmberRestPath {

    public static final String C_PATH = "p1_rest_path";
    public static final String C_OFFER = "p1_rest_path_offer";

    public static final int NONE = 0;
    /** Table rushHeal */
    public static final int FULL = 1;
    /** Half of table */
    public static final int LIGHT = 2;
    /** No between-boss heal */
    public static final int BARE = 3;

    private EmberRestPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FULL || id == LIGHT || id == BARE;
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
        if ("full".equals(s) || "满休".equals(s) || "1".equals(s)) return FULL;
        if ("light".equals(s) || "轻休".equals(s) || "2".equals(s)) return LIGHT;
        if ("bare".equals(s) || "off".equals(s) || "不休".equals(s) || "3".equals(s)) return BARE;
        return -1;
    }

    public static String key(int id) {
        if (id == FULL) return "full";
        if (id == LIGHT) return "light";
        if (id == BARE) return "bare";
        return "none";
    }

    public static String label(int id) {
        if (id == FULL) return "休整·满休";
        if (id == LIGHT) return "休整·轻休";
        if (id == BARE) return "休整·不休";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FULL) return "连战/前哨阶段间按表回血";
        if (id == LIGHT) return "连战/前哨阶段间只回表比例的一半";
        if (id == BARE) return "连战/前哨阶段间不回血 · 硬核衔接";
        return "点选连战休整回血偏好（改打法 · 不抬表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8休整路径：首通 Q04 后可选（连战/前哨）";
        if (!valid(id)) return "§8休整路径：未选 · /corerpg p1 restpath";
        return "§e休整路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Never raises above table. FULL/NONE → table; LIGHT → table/2; BARE → 0. */
    public static double effectiveHeal(int path, double tableHeal) {
        double t = Math.max(0.0, Math.min(1.0, tableHeal));
        if (path == BARE) return 0.0;
        if (path == LIGHT) return t * 0.5;
        return t;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选连战休整（阶段间回血 · 可随时改 · 不抬表；残响单首领无阶段）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[满休]", "/corerpg p1 restpath full", tip(FULL), "GREEN"},
                new String[]{"[轻休]", "/corerpg p1 restpath light", tip(LIGHT), "GOLD"},
                new String[]{"[不休]", "/corerpg p1 restpath bare", tip(BARE), "GRAY"},
                new String[]{"[取消]", "/corerpg p1 restpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 100L);
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
            p.sendMessage(EmberRunService.P + "§c休整路径需本人首通 Q04（连战/前哨才有阶段休整）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空休整路径（按表回血）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        double table = 0.3; // display default; real maps may differ
        double eff = effectiveHeal(id, table);
        p.sendMessage(P + "§a休整 → §f" + label(id) + " §7· " + tip(id)
                + " §8（例表 " + Math.round(table * 100) + "% → 你 " + Math.round(eff * 100) + "%）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "连战/前哨阶段间按偏好回血", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 restpath", "重选", "GRAY"});
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
