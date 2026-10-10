package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D533: friend online ping path — open / gate / busy.
 * NEW play: sticky policy for friend-online awareness (live ping vs join digest vs mute).
 * Distinct from FriendPath (accept), Party/Guild/Mentor invite policies. Chat only.
 * Zero power / AFK / sx / forge / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberPingPath {

    public static final String C_PATH = "p1_ping_path";
    public static final String C_OFFER = "p1_ping_path_offer";

    public static final int NONE = 0;
    public static final int OPEN = 1;
    public static final int GATE = 2;
    public static final int BUSY = 3;

    private EmberPingPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == OPEN || id == GATE || id == BUSY;
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
        if ("open".equals(s) || "auto".equals(s) || "敞开".equals(s) || "实时".equals(s) || "1".equals(s)) return OPEN;
        if ("gate".equals(s) || "ask".equals(s) || "汇总".equals(s) || "摘要".equals(s) || "2".equals(s)) return GATE;
        if ("busy".equals(s) || "mute".equals(s) || "静拒".equals(s) || "静默".equals(s) || "3".equals(s)) return BUSY;
        return -1;
    }

    public static String key(int id) {
        if (id == OPEN) return "open";
        if (id == GATE) return "gate";
        if (id == BUSY) return "busy";
        return "none";
    }

    public static String label(int id) {
        if (id == OPEN) return "上线·敞开";
        if (id == GATE) return "上线·汇总";
        if (id == BUSY) return "上线·静拒";
        return "未选";
    }

    public static String tip(int id) {
        if (id == OPEN) return "好友上线时立刻聊天提醒";
        if (id == GATE) return "自己进服时汇总一次在线好友（默认）";
        if (id == BUSY) return "不提醒好友上线";
        return "点选好友上线提醒节奏（改社交感知 · 不加属性）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8上线路径：首通 Q01 后可选";
        if (!valid(id)) return "§8上线路径：未选 · /corerpg p1 pingpath";
        return "§e上线路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldLivePing(int path) { return path == OPEN; }
    public static boolean shouldLivePing(PlayerData d) { return shouldLivePing(get(d)); }

    public static boolean shouldDigest(int path) { return path == GATE; }
    public static boolean shouldDigest(PlayerData d) { return shouldDigest(get(d)); }

    public static boolean shouldMute(int path) { return path == BUSY; }

    /** LIVE: notify watchers who have OPEN and list joiner as friend. */
    public static void notifyWatchers(Player joiner) {
        if (joiner == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getFriendService() == null || !pl.getFriendService().isEnabled()) return;
        String name = joiner.getName();
        for (Player watcher : Bukkit.getOnlinePlayers()) {
            if (watcher.getUniqueId().equals(joiner.getUniqueId())) continue;
            PlayerData wd = pl.getDataStore().get(watcher.getUniqueId());
            if (wd == null || !shouldLivePing(wd)) continue;
            if (!pl.getFriendService().isFriendOf(wd, name)) continue;
            watcher.sendMessage(EmberRunService.P + "§a上线·敞开 §7好友 §f" + name + " §7上线了");
        }
    }

    /** GATE: one digest of currently online friends after join. */
    public static void maybeDigestOnJoin(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getFriendService() == null || !pl.getFriendService().isEnabled()) return;
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null || !shouldDigest(d)) return;
        List<String> online = pl.getFriendService().onlineFriendNames(d);
        if (online.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (String fn : online) {
            if (n >= 6) { sb.append(" §8…"); break; }
            if (n > 0) sb.append("§7, ");
            sb.append("§f").append(fn);
            n++;
        }
        p.sendMessage(EmberRunService.P + "§e上线·汇总 §7在线好友 §f" + online.size() + " §7人：" + sb);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选好友上线提醒节奏（改社交感知 · 可随时改 · 不加属性）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[敞开]", "/corerpg p1 pingpath open", tip(OPEN), "GREEN"},
                new String[]{"[汇总]", "/corerpg p1 pingpath gate", tip(GATE), "GOLD"},
                new String[]{"[静拒]", "/corerpg p1 pingpath busy", tip(BUSY), "RED"},
                new String[]{"[取消]", "/corerpg p1 pingpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 525L); // after title ~510
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
            p.sendMessage(EmberRunService.P + "§c上线路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空上线路径（按默认汇总）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a上线 → §f" + label(id) + " §7· " + tip(id));
        if (id == GATE) maybeDigestOnJoin(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[好友]", "/corerpg friend", "看好友列表", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 pingpath", "重选", "GRAY"});
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
