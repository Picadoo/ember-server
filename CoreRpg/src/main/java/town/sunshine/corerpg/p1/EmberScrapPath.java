package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.ScrapService;

/**
 * D538: legacy scrap path — auto / ask / mute.
 * NEW acquisition: sticky policy to scrap enhance-0 whitelist blade/charm in backpack → mats
 * after settle / on join. Distinct from JunkPath (gearlib→blanks), StashPath, DealPath.
 * Chat only. Zero scrap.yml rewrite / AFK / sx / forge spend-chase / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberScrapPath {

    public static final String C_PATH = "p1_scrap_path";
    public static final String C_OFFER = "p1_scrap_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberScrapPath() {}

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
        if ("ask".equals(s) || "提醒".equals(s) || "手点".equals(s) || "2".equals(s)) return ASK;
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
        if (id == AUTO) return "分解·自动";
        if (id == ASK) return "分解·提醒";
        if (id == MUTE) return "分解·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "结算/进服时自动分解背包未强化旧刃护符";
        if (id == ASK) return "有可分解旧件时聊天提醒，点按钮分解（默认）";
        if (id == MUTE) return "不刷提醒；可手持 /corerpg scrap";
        return "点选背包旧件分解节奏（改材料获取 · 不改分解表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8分解路径：首通 Q01 后可选";
        if (!valid(id)) return "§8分解路径：未选 · /corerpg p1 scrappath";
        return "§e分解路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static void maybeAfterProgress(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        ScrapService scrap = scrap();
        if (scrap == null || !scrap.isEnabled()) return;
        int n = scrap.countPathScrap(p);
        if (n <= 0) return;
        int path = get(d);
        if (shouldAuto(path)) {
            int ok = scrap.pathBulkScrap(p);
            if (ok > 0) p.sendMessage(EmberRunService.P + "§a分解·自动 §7已分解背包旧件 §f" + ok + " §7件 → 材料");
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e背包有可分解旧件 §f" + n + " §e件 ",
                new String[]{"[一键分解]", "/corerpg p1 scrap", "分解未强化白名单刃/符（留最后一把武器）", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 scrappath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选背包旧件分解节奏（改获取 · 可随时改 · 不改分解表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 scrappath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 scrappath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 scrappath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 scrappath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 600L); // after deal ~585
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
            p.sendMessage(EmberRunService.P + "§c分解路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空分解路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a分解 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeAfterProgress(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[手持分解]", "/corerpg scrap", "预览手持刃/符", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 scrappath", "重选", "GRAY"});
    }

    private static ScrapService scrap() {
        CoreRpgPlugin pl = plugin();
        return pl == null ? null : pl.getScrapService();
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
