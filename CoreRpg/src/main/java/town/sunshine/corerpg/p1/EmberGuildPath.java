package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D527: guild-invite accept path — open / gate / busy.
 * NEW play+acquisition: sticky policy for incoming 盟约 invites (auto-join / ask / auto-refuse).
 * Distinct from FriendPath / MentorPath / RecruitPath. Chat only. Zero power / AFK / sx.
 * Not combo sticky, not forge chase, not enter/card/ActionBar cue.
 */
public final class EmberGuildPath {

    public static final String C_PATH = "p1_guild_path";
    public static final String C_OFFER = "p1_guild_path_offer";

    public static final int NONE = 0;
    public static final int OPEN = 1;
    public static final int GATE = 2;
    public static final int BUSY = 3;

    private EmberGuildPath() {}

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
        if ("open".equals(s) || "auto".equals(s) || "敞开".equals(s) || "自动".equals(s) || "1".equals(s)) return OPEN;
        if ("gate".equals(s) || "ask".equals(s) || "审核".equals(s) || "手动".equals(s) || "2".equals(s)) return GATE;
        if ("busy".equals(s) || "deny".equals(s) || "静拒".equals(s) || "拒收".equals(s) || "3".equals(s)) return BUSY;
        return -1;
    }

    public static String key(int id) {
        if (id == OPEN) return "open";
        if (id == GATE) return "gate";
        if (id == BUSY) return "busy";
        return "none";
    }

    public static String label(int id) {
        if (id == OPEN) return "盟约·敞开";
        if (id == GATE) return "盟约·审核";
        if (id == BUSY) return "盟约·静拒";
        return "未选";
    }

    public static String tip(int id) {
        if (id == OPEN) return "有人邀请入盟时自动加入";
        if (id == GATE) return "入盟邀请需你点同意（默认）";
        if (id == BUSY) return "自动拒收入盟邀请（不留待办）";
        return "点选盟约邀请处理方式（改节奏 · 不改战力）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8盟约路径：首通 Q01 后可选";
        if (!valid(id)) return "§8盟约路径：未选 · /corerpg p1 guildpath";
        return "§e盟约路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAutoAccept(int path) { return path == OPEN; }
    public static boolean shouldAutoAccept(PlayerData d) { return shouldAutoAccept(get(d)); }

    public static boolean shouldAutoDeny(int path) { return path == BUSY; }
    public static boolean shouldAutoDeny(PlayerData d) { return shouldAutoDeny(get(d)); }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选盟约邀请处理方式（改节奏 · 可随时改 · 不改战力）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[敞开]", "/corerpg p1 guildpath open", tip(OPEN), "GREEN"},
                new String[]{"[审核]", "/corerpg p1 guildpath gate", tip(GATE), "GOLD"},
                new String[]{"[静拒]", "/corerpg p1 guildpath busy", tip(BUSY), "RED"},
                new String[]{"[取消]", "/corerpg p1 guildpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 435L); // after stash ~420
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
            p.sendMessage(EmberRunService.P + "§c盟约路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空盟约路径（按默认审核）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a盟约 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[盟约]", "/corerpg guild", "看盟约", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 guildpath", "重选", "GRAY"});
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
