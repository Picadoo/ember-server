package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D518: online-time reward claim path — auto / ask / mute.
 * NEW acquisition: sticky policy when a daily online milestone is reached (auto-claim vs tip vs silent).
 * Distinct from ClaimPath (ledger deliver) and Sign-in. Chat only. Zero AFK rates / sx / power.
 * Not combo sticky, not forge/enter/ActionBar cue.
 */
public final class EmberOnlinePath {

    public static final String C_PATH = "p1_online_path";
    public static final String C_OFFER = "p1_online_path_offer";

    public static final int NONE = 0;
    /** Auto-claim the milestone as soon as it unlocks */
    public static final int AUTO = 1;
    /** Stock: tip button to claim in menu */
    public static final int ASK = 2;
    /** No tip; player claims manually later (still auto-settled next day) */
    public static final int MUTE = 3;

    private EmberOnlinePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == AUTO || id == ASK || id == MUTE;
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
        if ("auto".equals(s) || "自动".equals(s) || "1".equals(s)) return AUTO;
        if ("ask".equals(s) || "提醒".equals(s) || "手领".equals(s) || "2".equals(s)) return ASK;
        if ("mute".equals(s) || "quiet".equals(s) || "静默".equals(s) || "3".equals(s)) return MUTE;
        return -1;
    }

    public static String key(int id) {
        if (id == AUTO) return "auto";
        if (id == ASK) return "ask";
        if (id == MUTE) return "mute";
        return "none";
    }

    public static String label(int id) {
        if (id == AUTO) return "在线·自动";
        if (id == ASK) return "在线·提醒";
        if (id == MUTE) return "在线·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "有效在线达标时自动领取当日时长奖励";
        if (id == ASK) return "达标时聊天提醒，点菜单领取（默认）";
        if (id == MUTE) return "达标不刷提醒；可稍后在签到页领取";
        return "点选在线时长奖励领取方式（改节奏 · 不改奖励表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8在线路路径：首通 Q01 后可选";
        if (!valid(id)) return "§8在线路路径：未选 · /corerpg p1 onlinepath";
        return "§e在线路路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAutoClaim(int path) { return path == AUTO; }
    public static boolean shouldAutoClaim(PlayerData d) { return shouldAutoClaim(get(d)); }

    public static boolean shouldMute(int path) { return path == MUTE; }
    public static boolean shouldMute(PlayerData d) { return shouldMute(get(d)); }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选在线时长奖励领取方式（改节奏 · 可随时改 · 不改奖励表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 onlinepath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 onlinepath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 onlinepath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 onlinepath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 330L); // after equip ~315
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
            p.sendMessage(EmberRunService.P + "§c在线路路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空在线路路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a在线 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[签到·在线]", "/corerpg p1 sign menu", "看时长与领取", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 onlinepath", "重选", "GRAY"});
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
