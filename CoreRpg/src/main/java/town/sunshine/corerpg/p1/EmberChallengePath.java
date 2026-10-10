package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D519: cleared-map challenge preference path — prefer / easy / follow.
 * NEW play+acquisition: sticky default for challenge vs normal when re-entering a first-cleared mainline map.
 * Distinct from FeaturedPath (weekly featured identity buttons only). Chat only. Zero power / AFK / sx.
 * Not combo sticky, not forge chase, not enter/card/ActionBar cue twin (rewrites enter flag, no ActionBar).
 */
public final class EmberChallengePath {

    public static final String C_PATH = "p1_challenge_path";
    public static final String C_OFFER = "p1_challenge_path_offer";

    public static final int NONE = 0;
    /** Prefer challenge when re-entering a cleared map (if challenge unlocked) */
    public static final int PREFER = 1;
    /** Prefer normal even if the command asked for challenge */
    public static final int EASY = 2;
    /** Honor the enter command as typed (stock) */
    public static final int FOLLOW = 3;

    private EmberChallengePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == PREFER || id == EASY || id == FOLLOW;
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
        if ("prefer".equals(s) || "challenge".equals(s) || "偏挑战".equals(s) || "挑战".equals(s) || "1".equals(s)) return PREFER;
        if ("easy".equals(s) || "normal".equals(s) || "偏普通".equals(s) || "普通".equals(s) || "2".equals(s)) return EASY;
        if ("follow".equals(s) || "ask".equals(s) || "随令".equals(s) || "随指令".equals(s) || "3".equals(s)) return FOLLOW;
        return -1;
    }

    public static String key(int id) {
        if (id == PREFER) return "prefer";
        if (id == EASY) return "easy";
        if (id == FOLLOW) return "follow";
        return "none";
    }

    public static String label(int id) {
        if (id == PREFER) return "硬本·偏挑战";
        if (id == EASY) return "硬本·偏普通";
        if (id == FOLLOW) return "硬本·随令";
        return "未选";
    }

    public static String tip(int id) {
        if (id == PREFER) return "已首通主线图默认进挑战版（可指令写普通）";
        if (id == EASY) return "已首通主线图默认进普通版（可指令写挑战）";
        if (id == FOLLOW) return "完全按进本指令：写 challenge 才挑战";
        return "点选已首通图挑战/普通默认（改获取节奏 · 不改掉落表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8硬本路径：首通 Q07 后可选";
        if (!valid(id)) return "§8硬本路径：未选 · /corerpg p1 challengepath";
        return "§e硬本路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Resolve challenge flag for a cleared mainline re-enter. Bukkit-free.
     * Only applies when challenge is unlocked and the map was first-cleared.
     * Raid/abyss/short callers should not use this.
     */
    public static boolean resolve(boolean requested, int path, boolean firstCleared, boolean challengeOpen) {
        if (!challengeOpen || !firstCleared) return requested;
        if (path == PREFER) return true;
        if (path == EASY) return false;
        return requested; // FOLLOW / NONE
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选已首通主线图默认（改获取节奏 · 可随时改 · 不改掉落表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[偏挑战]", "/corerpg p1 challengepath prefer", tip(PREFER), "RED"},
                new String[]{"[偏普通]", "/corerpg p1 challengepath easy", tip(EASY), "GREEN"},
                new String[]{"[随令]", "/corerpg p1 challengepath follow", tip(FOLLOW), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 challengepath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 195L); // after recruit ~180
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
            p.sendMessage(EmberRunService.P + "§c硬本路径需本人首通 Q07。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空硬本路径（按进本指令）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a硬本 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/ember_p1_adventure", "进已首通图试默认", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 challengepath", "重选", "GRAY"});
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
