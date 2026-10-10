package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D530: dungeon-team invite accept path — open / gate / busy.
 * NEW play: sticky policy for incoming friend party invites (DungeonPlus).
 * Distinct from RecruitPath (leader auto-accepts join requests), FriendPath, GuildPath.
 * Chat only. Zero power / AFK / sx / forge / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberPartyPath {

    public static final String C_PATH = "p1_party_path";
    public static final String C_OFFER = "p1_party_path_offer";

    public static final int NONE = 0;
    public static final int OPEN = 1;
    public static final int GATE = 2;
    public static final int BUSY = 3;

    private EmberPartyPath() {}

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
        if (id == OPEN) return "组队·敞开";
        if (id == GATE) return "组队·审核";
        if (id == BUSY) return "组队·静拒";
        return "未选";
    }

    public static String tip(int id) {
        if (id == OPEN) return "好友组队邀请时自动入队";
        if (id == GATE) return "组队邀请需你点同意（默认）";
        if (id == BUSY) return "自动拒收好友组队邀请";
        return "点选好友组队邀请处理方式（改组队节奏 · 不改战力）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8组队路径：首通 Q01 后可选";
        if (!valid(id)) return "§8组队路径：未选 · /corerpg p1 partypath";
        return "§e组队路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAutoAccept(int path) { return path == OPEN; }
    public static boolean shouldAutoAccept(PlayerData d) { return shouldAutoAccept(get(d)); }

    public static boolean shouldAutoDeny(int path) { return path == BUSY; }
    public static boolean shouldAutoDeny(PlayerData d) { return shouldAutoDeny(get(d)); }

    /** Invitee-side: try accept leader invite via DP request join. */
    public static void tryAutoAcceptInvite(Player invitee, String leaderName) {
        if (invitee == null || leaderName == null || leaderName.isEmpty()) return;
        invitee.performCommand("dungeon-team request join " + leaderName);
        invitee.sendMessage(EmberRunService.P + "§a组队·敞开 §7已自动接受 §f" + leaderName + " §7的组队邀请");
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选好友组队邀请处理方式（改节奏 · 可随时改 · 不改战力）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[敞开]", "/corerpg p1 partypath open", tip(OPEN), "GREEN"},
                new String[]{"[审核]", "/corerpg p1 partypath gate", tip(GATE), "GOLD"},
                new String[]{"[静拒]", "/corerpg p1 partypath busy", tip(BUSY), "RED"},
                new String[]{"[取消]", "/corerpg p1 partypath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 480L); // after trail ~465
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
            p.sendMessage(EmberRunService.P + "§c组队路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空组队路径（按默认审核）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a组队 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[好友]", "/corerpg friend", "邀请好友组队", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 partypath", "重选", "GRAY"});
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
