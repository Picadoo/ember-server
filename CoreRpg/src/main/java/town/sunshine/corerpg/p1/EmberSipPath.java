package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D548: combat sip path — auto / ask / mute.
 * NEW dungeon combat loop: sticky drink of 余烬回复药 when HP < 40% in P1 worlds.
 * Distinct from PrepPath (town buy), BrewPath (hub craft), DosePath (stamina), BitePath (food).
 * Chat only for ASK; MUTE suppresses low-HP ActionBar polish. Zero dmg/CD table / AFK / sx /
 * forge / combo sticky / enter-card / new ActionBar cue twin.
 */
public final class EmberSipPath {

    public static final String C_PATH = "p1_sip_path";
    public static final String C_OFFER = "p1_sip_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private static final long ASK_COOLDOWN_MS = 10_000L;
    private static final Map<UUID, Long> lastAsk = new ConcurrentHashMap<UUID, Long>();

    private EmberSipPath() {}

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
        if (id == AUTO) return "喝药·自动";
        if (id == ASK) return "喝药·提醒";
        if (id == MUTE) return "喝药·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "副本内生命低于40%且有回复药时自动喝一瓶";
        if (id == ASK) return "低血时聊天提醒，点按钮喝药（默认）";
        if (id == MUTE) return "不刷低血提醒；手动右键喝药";
        return "点选副本低血喝药节奏（改战斗续航 · 不改药表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8喝药路径：首通 Q01 后可选";
        if (!valid(id)) return "§8喝药路径：未选 · /corerpg p1 sippath";
        return "§e喝药路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    /** MUTE: suppress existing low-HP ActionBar polish (no new ActionBar cue). */
    public static boolean suppressLowHpBar(PlayerData d) {
        return shouldMute(get(d));
    }

    /** Called from set HUD tick while in P1 combat. */
    public static void maybeAfterLowHp(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        EmberSupplyService sup = supplies();
        if (sup == null || !sup.needsSip(p)) return;
        int path = get(d);
        if (shouldAuto(path)) {
            if (sup.pathSipOne(p)) {
                p.sendMessage(EmberRunService.P + "§a喝药·自动 §7已喝回复药");
            }
            return;
        }
        if (shouldMute(path)) return;
        long now = System.currentTimeMillis();
        Long last = lastAsk.get(p.getUniqueId());
        if (last != null && now - last < ASK_COOLDOWN_MS) return;
        lastAsk.put(p.getUniqueId(), now);
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e生命偏低，可喝回复药 ",
                new String[]{"[喝药]", "/corerpg p1 sip", "立刻喝一瓶（冷却共享）", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 sippath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选副本低血喝药节奏（改战斗 · 可随时改 · 不改药表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 sippath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 sippath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 sippath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 sippath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 750L); // after bread ~735
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
            p.sendMessage(EmberRunService.P + "§c喝药路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空喝药路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a喝药 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeAfterLowHp(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[补给]", "/corerpg p1 shop", "买回复药", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 sippath", "重选", "GRAY"});
    }

    private static EmberSupplyService supplies() {
        CoreRpgPlugin pl = plugin();
        return pl == null ? null : pl.getEmberSupplies();
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
