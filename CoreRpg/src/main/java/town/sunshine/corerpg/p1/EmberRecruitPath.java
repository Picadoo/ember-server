package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D513: raid recruit accept path — open / gate / mute.
 * NEW play+acquisition: leader sticky for auto-accepting DP join requests (and mute login board).
 * Distinct from ModePath (echo/outpost/pledge route). Chat only. Zero power / AFK / sx.
 * Not combo sticky, not forge chase, not enter/card/ActionBar cue.
 */
public final class EmberRecruitPath {

    public static final String C_PATH = "p1_recruit_path";
    public static final String C_OFFER = "p1_recruit_path_offer";

    public static final int NONE = 0;
    /** Auto-accept dungeon-team join requests while leading */
    public static final int OPEN = 1;
    /** Stock: leader clicks [同意] (default) */
    public static final int GATE = 2;
    /** Skip Q07 login recruit-board flash; still GATE for accepts */
    public static final int MUTE = 3;

    private EmberRecruitPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == OPEN || id == GATE || id == MUTE;
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
        if ("mute".equals(s) || "quiet".equals(s) || "静默".equals(s) || "3".equals(s)) return MUTE;
        return -1;
    }

    public static String key(int id) {
        if (id == OPEN) return "open";
        if (id == GATE) return "gate";
        if (id == MUTE) return "mute";
        return "none";
    }

    public static String label(int id) {
        if (id == OPEN) return "招募·敞开";
        if (id == GATE) return "招募·审核";
        if (id == MUTE) return "招募·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == OPEN) return "有人申请入队时自动同意（你当队长时）";
        if (id == GATE) return "入队申请需你点同意（默认）";
        if (id == MUTE) return "上线不刷招募板；入队仍需手动同意";
        return "点选团本招募偏好（改组队手感 · 不改掉落）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8招募路径：首通 Q07 后可选";
        if (!valid(id)) return "§8招募路径：未选 · /corerpg p1 recruitpath";
        return "§e招募路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** True when leader should auto-accept this applicant. */
    public static boolean shouldAutoAccept(PlayerData leader) {
        return get(leader) == OPEN;
    }

    /** True when login board flash should be skipped. */
    public static boolean shouldMuteLoginBoard(PlayerData d) {
        return get(d) == MUTE;
    }

    /**
     * After applicant sent DP join request: if leader path is OPEN, accept shortly.
     * Bukkit-free decision; schedule lives in RecruitService.
     */
    public static boolean wantsAutoAccept(int path) {
        return path == OPEN;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选团本招募偏好（改组队手感 · 可随时改 · 不改掉落）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[敞开]", "/corerpg p1 recruitpath open", tip(OPEN), "GREEN"},
                new String[]{"[审核]", "/corerpg p1 recruitpath gate", tip(GATE), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 recruitpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 recruitpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 180L); // after fail/fee ~140–160
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
            p.sendMessage(EmberRunService.P + "§c招募路径需本人首通 Q07。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空招募路径（按默认审核）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a招募 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[发招募]", "/corerpg p1 recruit r01", "挂板找队友", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 recruitpath", "重选", "GRAY"});
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
