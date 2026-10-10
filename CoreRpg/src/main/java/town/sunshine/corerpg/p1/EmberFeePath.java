package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D511: abyss fee pay path — coin / mark / auto.
 * NEW acquisition: chooses whether segment fee prefers 余烬币 or surplus T3 marks (C08).
 * Never changes fee amounts / fee_mark_coin. Chat only. Not forge spend-chase, not clamp twin,
 * not combo sticky, not enter/ActionBar cue. No K3/AFK/sx20.
 */
public final class EmberFeePath {

    public static final String C_PATH = "p1_fee_path";
    public static final String C_OFFER = "p1_fee_path_offer";

    public static final int NONE = 0;
    /** Prefer coin when balance covers fee (stock) */
    public static final int COIN = 1;
    /** Prefer surplus T3 marks even when coin would cover */
    public static final int MARK = 2;
    /** Same as coin/stock — explicit latch */
    public static final int AUTO = 3;

    private EmberFeePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == COIN || id == MARK || id == AUTO;
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
        if ("coin".equals(s) || "币".equals(s) || "币付".equals(s) || "1".equals(s)) return COIN;
        if ("mark".equals(s) || "印".equals(s) || "印付".equals(s) || "2".equals(s)) return MARK;
        if ("auto".equals(s) || "自动".equals(s) || "3".equals(s)) return AUTO;
        return -1;
    }

    public static String key(int id) {
        if (id == COIN) return "coin";
        if (id == MARK) return "mark";
        if (id == AUTO) return "auto";
        return "none";
    }

    public static String label(int id) {
        if (id == COIN) return "层费·币付";
        if (id == MARK) return "层费·印付";
        if (id == AUTO) return "层费·自动";
        return "未选";
    }

    public static String tip(int id) {
        if (id == COIN) return "深渊层费优先花余烬币（不够再用印记）";
        if (id == MARK) return "深渊层费优先花多余 T3 印记（保留兑换底仓）";
        if (id == AUTO) return "深渊层费按默认：有币先币、不够再印";
        return "点选深渊层费支付偏好（改获取消耗 · 不改价表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8层费路径：首通 Q07 后可选（深渊）";
        if (!valid(id)) return "§8层费路径：未选 · /corerpg p1 feepath";
        return "§e层费路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** True when spend should try marks before coin. */
    public static boolean preferMark(int path) {
        return path == MARK;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选深渊层费支付（币 / 多余 T3 印记 · 可随时改 · 不改费用表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[币付]", "/corerpg p1 feepath coin", tip(COIN), "GREEN"},
                new String[]{"[印付]", "/corerpg p1 feepath mark", tip(MARK), "GOLD"},
                new String[]{"[自动]", "/corerpg p1 feepath auto", tip(AUTO), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 feepath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 160L); // after fail ~140L
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
            p.sendMessage(EmberRunService.P + "§c层费路径需本人首通 Q07（深渊才有层费）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空层费路径（按默认币优先）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a层费 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[深渊]", "/corerpg p1 abyss", "进层时按偏好扣费", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 feepath", "重选", "GRAY"});
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
