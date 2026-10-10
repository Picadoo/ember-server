package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D468: weekly echo-residual boss path — pick which echo_q0N to chase.
 * Real content route after Q04; zero stamp / AFK / power change.
 */
public final class EmberEchoPath {

    public static final String C_PATH = "p1_echo_path";
    public static final String C_OFFER = "p1_echo_path_offer";
    public static final String UNLOCK = "q04";

    public static final int NONE = 0;

    private static final String[] LABELS = {
            null,
            "残门蛮兵", "守炉蛮兵", "残誓守卫", "潮闸重卫",
            "礁卫", "霜潮", "炉心"
    };

    private EmberEchoPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int n) {
        return n >= 1 && n <= 7;
    }

    public static boolean set(PlayerData d, int n) {
        if (d == null) return false;
        if (n != NONE && !valid(n)) return false;
        int cur = d.periodCount(C_PATH, "all");
        if (cur == n) return false;
        d.addPeriodCount(C_PATH, "all", n - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if (s.startsWith("echo_q")) s = s.substring(5); // q01
        if (s.startsWith("q") && s.length() == 3) {
            try {
                int n = Integer.parseInt(s.substring(1));
                return valid(n) ? n : -1;
            } catch (NumberFormatException e) { return -1; }
        }
        try {
            int n = Integer.parseInt(s);
            return valid(n) ? n : -1;
        } catch (NumberFormatException e) { return -1; }
    }

    public static String mapKey(int n) {
        return valid(n) ? "echo_q0" + n : null;
    }

    public static String label(int n) {
        if (!valid(n)) return "未选";
        return "Q0" + n + " " + LABELS[n];
    }

    public static String tip(int n) {
        if (!valid(n)) return "点选本周追哪只残响首领";
        return "去残响 · " + label(n) + " · 周池 3 次有奖徽记";
    }

    public static String glance(int n, boolean unlocked) {
        if (!unlocked) return "§8残响路径：首通 Q04 后可选";
        if (!valid(n)) return "§8残响路径：未选 · /corerpg p1 echopath";
        return "§e残响路径：§f" + label(n);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周残响追哪只首领（七图共用周 3 次有奖 · 可随时改）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[Q01]", "/corerpg p1 echopath q01", tip(1), "AQUA"},
                new String[]{"[Q02]", "/corerpg p1 echopath q02", tip(2), "GOLD"},
                new String[]{"[Q03]", "/corerpg p1 echopath q03", tip(3), "YELLOW"},
                new String[]{"[Q04]", "/corerpg p1 echopath q04", tip(4), "GREEN"});
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[Q05]", "/corerpg p1 echopath q05", tip(5), "LIGHT_PURPLE"},
                new String[]{"[Q06]", "/corerpg p1 echopath q06", tip(6), "BLUE"},
                new String[]{"[Q07]", "/corerpg p1 echopath q07", tip(7), "RED"},
                new String[]{"[打开残响厅]", "/corerpg p1 modes", "七图选厅", "GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        offerPick(p);
        return true;
    }

    /** After Q04 unlock announce (~3s). */
    public static void scheduleOffer(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offerOnce(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offerOnce(p);
        }, 55L);
    }

    static void offerOnce(Player p) {
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_OFFER, "all") > 0) return; // first-unlock latch (also weekly uses week key)
        d.addPeriodCount(C_OFFER, "all", 1);
        flush(p);
        offerPick(p);
    }

    public static void forceOffer(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int a = d.periodCount(C_OFFER, "all");
            if (a > 0) d.addPeriodCount(C_OFFER, "all", -a);
            flush(p);
        }
        offerPick(p);
    }

    private static PlayerData dataOf(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return null;
        EmberRunService runs = ((town.sunshine.corerpg.CoreRpgPlugin) pl).getEmberRuns();
        return runs == null ? null : runs.dataOf(p.getUniqueId());
    }

    private static void flush(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
            ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }
}
