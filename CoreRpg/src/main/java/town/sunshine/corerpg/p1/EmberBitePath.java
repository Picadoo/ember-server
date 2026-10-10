package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.LifeService;
import town.sunshine.corerpg.PlayerData;

/**
 * D544: food bite path — auto / ask / mute.
 * NEW combat sustain: sticky policy to eat backpack 炭烤鱼 when hunger is low.
 * Distinct from CookPath (make food), PrepPath (heal bottles), DosePath (stamina). Chat only.
 * Zero food tables / AFK / sx / forge / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberBitePath {

    public static final String C_PATH = "p1_bite_path";
    public static final String C_OFFER = "p1_bite_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberBitePath() {}

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
        if (id == AUTO) return "果腹·自动";
        if (id == ASK) return "果腹·提醒";
        if (id == MUTE) return "果腹·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "饱食偏低且有炭烤鱼时自动吃一口";
        if (id == ASK) return "缺饱食且有烤鱼时聊天提醒，点按钮吃（默认）";
        if (id == MUTE) return "不刷提醒；可手持右键吃";
        return "点选果腹节奏（改战斗续航 · 不改食物表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8果腹路径：首通 Q01 后可选";
        if (!valid(id)) return "§8果腹路径：未选 · /corerpg p1 bitepath";
        return "§e果腹路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static void maybeAfterProgress(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        LifeService life = life();
        if (life == null || !life.needsBite(p)) return;
        int path = get(d);
        int n = life.countPathFood(p);
        if (shouldAuto(path)) {
            int ok = life.pathBiteOne(p);
            if (ok > 0) {
                p.sendMessage(EmberRunService.P + "§a果腹·自动 §7吃了炭烤鱼 §f饱食 "
                        + p.getFoodLevel() + "/20");
            }
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e饱食偏低且有炭烤鱼 §f" + n + " §e份 ",
                new String[]{"[吃一口]", "/corerpg p1 bite", "吃背包炭烤鱼回饱食", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 bitepath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选果腹节奏（改续航 · 可随时改 · 不改食物表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 bitepath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 bitepath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 bitepath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 bitepath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 690L); // after cook ~675
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
            p.sendMessage(EmberRunService.P + "§c果腹路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空果腹路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a果腹 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeAfterProgress(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[代烤]", "/corerpg p1 cookpath", "先有烤鱼", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 bitepath", "重选", "GRAY"});
    }

    private static LifeService life() {
        CoreRpgPlugin pl = plugin();
        return pl == null ? null : pl.getLifeService();
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
