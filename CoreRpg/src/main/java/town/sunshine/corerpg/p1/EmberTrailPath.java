package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D529: cosmetic trail wear path — auto / ask / mute.
 * NEW play: sticky policy to auto-wear an owned footprint when none is on (join / unlock).
 * Distinct from Pet/Feed paths. Chat only. Zero power / AFK / sx / forge / combo / enter-card cue.
 */
public final class EmberTrailPath {

    public static final String C_PATH = "p1_trail_path";
    public static final String C_OFFER = "p1_trail_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberTrailPath() {}

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
        if ("auto".equals(s) || "自动".equals(s) || "装上".equals(s) || "1".equals(s)) return AUTO;
        if ("ask".equals(s) || "提醒".equals(s) || "手装".equals(s) || "2".equals(s)) return ASK;
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
        if (id == AUTO) return "足迹·自动";
        if (id == ASK) return "足迹·提醒";
        if (id == MUTE) return "足迹·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "有足迹却未装时自动装上第一件";
        if (id == ASK) return "可装足迹时聊天提醒（默认）";
        if (id == MUTE) return "不自动装、不提醒；可 /corerpg p1 trail";
        return "点选足迹装扮节奏（改出场 · 不加属性）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8足迹路径：首通 Q01 后可选";
        if (!valid(id)) return "§8足迹路径：未选 · /corerpg p1 trailpath";
        return "§e足迹路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static void maybeAfterProgress(Player p, EmberCosmetics cos) {
        if (p == null || cos == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        EmberCosmetics.Cosmetic c = cos.firstUnequippedTrail(d);
        if (c == null) return;
        int path = get(d);
        if (shouldAuto(path)) {
            if (cos.wearTrail(p, d, c.id))
                p.sendMessage(EmberRunService.P + "§a足迹·自动 §7已装上");
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e可装足迹「" + c.label + "§e」 ",
                new String[]{"[装上]", "/corerpg p1 trail " + c.id, "装上这件足迹", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 trailpath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选足迹装扮节奏（改出场 · 可随时改 · 不加属性）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 trailpath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 trailpath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 trailpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 trailpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 465L); // after feed ~450
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
            p.sendMessage(EmberRunService.P + "§c足迹路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空足迹路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a足迹 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO && runs.cosmetics() != null)
            maybeAfterProgress(p, runs.cosmetics());
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[足迹]", "/corerpg p1 trail", "看荣誉足迹", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 trailpath", "重选", "GRAY"});
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
