package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.EnhanceService;
import town.sunshine.corerpg.PlayerData;

/**
 * D535: combat gem preference path — sharp / steady / drain / gale.
 * NEW combat+economy: sticky preferred socket gem; fills empty unlocked hand sockets from backpack (join/hub).
 * Distinct from forge enhance spend-chase, scrap/junk blank loop, cosmetic wear. Chat only.
 * Zero enhance rates / AFK / sx / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberGemPath {

    public static final String C_PATH = "p1_gem_path";
    public static final String C_OFFER = "p1_gem_path_offer";

    public static final int NONE = 0;
    public static final int SHARP = 1;   // gem_ember_sharp — phys damage
    public static final int STEADY = 2;  // gem_ember_steady — defense
    public static final int DRAIN = 3;   // gem_ember_drain — life steal
    public static final int GALE = 4;    // gem_ember_gale — move speed

    public static final String GEM_SHARP = "gem_ember_sharp";
    public static final String GEM_STEADY = "gem_ember_steady";
    public static final String GEM_DRAIN = "gem_ember_drain";
    public static final String GEM_GALE = "gem_ember_gale";

    private EmberGemPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == SHARP || id == STEADY || id == DRAIN || id == GALE;
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
        if ("sharp".equals(s) || "锋刃".equals(s) || "攻击".equals(s) || "1".equals(s)) return SHARP;
        if ("steady".equals(s) || "稳御".equals(s) || "防御".equals(s) || "2".equals(s)) return STEADY;
        if ("drain".equals(s) || "汲魂".equals(s) || "吸血".equals(s) || "3".equals(s)) return DRAIN;
        if ("gale".equals(s) || "疾风".equals(s) || "移速".equals(s) || "4".equals(s)) return GALE;
        return -1;
    }

    public static String key(int id) {
        if (id == SHARP) return "sharp";
        if (id == STEADY) return "steady";
        if (id == DRAIN) return "drain";
        if (id == GALE) return "gale";
        return "none";
    }

    public static String label(int id) {
        if (id == SHARP) return "宝石·锋刃";
        if (id == STEADY) return "宝石·稳御";
        if (id == DRAIN) return "宝石·汲魂";
        if (id == GALE) return "宝石·疾风";
        return "未选";
    }

    public static String tip(int id) {
        if (id == SHARP) return "空孔优先嵌锋刃石（物伤）· 背包有石时自动嵌";
        if (id == STEADY) return "空孔优先嵌稳御石（物防）· 背包有石时自动嵌";
        if (id == DRAIN) return "空孔优先嵌汲魂石（吸血）· 背包有石时自动嵌";
        if (id == GALE) return "空孔优先嵌疾风石（移速）· 背包有石时自动嵌";
        return "点选战斗宝石偏好（改镶嵌节奏 · 不改强化表）";
    }

    public static String gemId(int id) {
        if (id == SHARP) return GEM_SHARP;
        if (id == STEADY) return GEM_STEADY;
        if (id == DRAIN) return GEM_DRAIN;
        if (id == GALE) return GEM_GALE;
        return null;
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8宝石路径：首通 Q01 后可选";
        if (!valid(id)) return "§8宝石路径：未选 · /corerpg p1 gempath";
        return "§e宝石路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static void maybeAfterProgress(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        int path = get(d);
        if (!valid(path)) return;
        String gid = gemId(path);
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEnhanceService() == null) return;
        EnhanceService enh = pl.getEnhanceService();
        if (!enh.hasEmptyUnlockedSocket(p)) return;
        if (enh.tryPathSocket(p, gid)) {
            p.sendMessage(EmberRunService.P + "§a宝石路径 §7已按「" + label(path) + "」镶嵌");
        }
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选战斗宝石偏好（改镶嵌 · 可随时改 · 不改强化率）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[锋刃]", "/corerpg p1 gempath sharp", tip(SHARP), "RED"},
                new String[]{"[稳御]", "/corerpg p1 gempath steady", tip(STEADY), "AQUA"},
                new String[]{"[汲魂]", "/corerpg p1 gempath drain", tip(DRAIN), "LIGHT_PURPLE"},
                new String[]{"[疾风]", "/corerpg p1 gempath gale", tip(GALE), "GREEN"},
                new String[]{"[取消]", "/corerpg p1 gempath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 555L); // after glow ~540
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
            p.sendMessage(EmberRunService.P + "§c宝石路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空宝石路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a宝石 → §f" + label(id) + " §7· " + tip(id));
        maybeAfterProgress(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[镶嵌]", "/corerpg socket", "看孔位", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 gempath", "重选", "GRAY"});
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
