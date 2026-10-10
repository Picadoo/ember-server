package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D508: variety-bounty chase path — affix / event / both.
 * NEW acquisition: ensures preferred variety outcome exists on repeat normals
 * so D144 花样委托 (affix/timed) can progress. After spice+room. Chat only.
 * Not spice kind-flavor twin (ensures presence for bounty, not blaze/control remap),
 * not room twin, not combo sticky, not forge/enter/ActionBar cue. Zero rates/tables.
 */
public final class EmberBountyPath {

    public static final String C_PATH = "p1_bounty_path";
    public static final String C_OFFER = "p1_bounty_path_offer";

    public static final int NONE = 0;
    /** Ensure an affix elite appears when missing */
    public static final int AFFIX = 1;
    /** Ensure a room event appears when missing */
    public static final int EVENT = 2;
    /** No ensure — glance both bounty lines only */
    public static final int BOTH = 3;

    private EmberBountyPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == AFFIX || id == EVENT || id == BOTH;
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
        if ("affix".equals(s) || "词缀".equals(s) || "elite".equals(s) || "1".equals(s)) return AFFIX;
        if ("event".equals(s) || "timed".equals(s) || "事件".equals(s) || "房间".equals(s) || "2".equals(s)) return EVENT;
        if ("both".equals(s) || "双追".equals(s) || "all".equals(s) || "3".equals(s)) return BOTH;
        return -1;
    }

    public static String key(int id) {
        if (id == AFFIX) return "affix";
        if (id == EVENT) return "event";
        if (id == BOTH) return "both";
        return "none";
    }

    public static String label(int id) {
        if (id == AFFIX) return "委托·词缀追";
        if (id == EVENT) return "委托·事件追";
        if (id == BOTH) return "委托·双追";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AFFIX) return "重打本尽量刷出词缀精英 · 推花样委托·词缀";
        if (id == EVENT) return "重打本尽量刷出房间事件 · 推花样委托·事件";
        if (id == BOTH) return "不强制补花样 · 自己兼顾两种委托";
        return "点选花样委托追猎（改获取 · 不改奖励表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8委托路径：首通 Q02 后可选（重打本花样委托）";
        if (!valid(id)) return "§8委托路径：未选 · /corerpg p1 bountypath";
        return "§e委托路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Ensure preferred variety outcome exists. Does not remap existing kinds (spice/room own that).
     * BOTH → no-op. Bukkit-free.
     */
    public static String[] applyEnsure(String[] rolled, int path, List<String> affixPool, List<String> eventPool, long seed) {
        String[] v = new String[4];
        v[0] = rolled != null && rolled.length > 0 && rolled[0] != null ? rolled[0] : "";
        v[1] = rolled != null && rolled.length > 1 && rolled[1] != null ? rolled[1] : "";
        v[2] = rolled != null && rolled.length > 2 && rolled[2] != null ? rolled[2] : "";
        v[3] = rolled != null && rolled.length > 3 && rolled[3] != null ? rolled[3] : (!v[2].isEmpty() ? "timed" : "");
        if (path == BOTH || !valid(path)) return v;
        Random r = new Random(seed);
        if (path == AFFIX && v[1].isEmpty()) {
            String pick = pick(affixPool, r);
            if (pick != null) {
                v[0] = v[0].isEmpty() ? roomPick(r) : v[0];
                v[1] = pick;
            }
            return v;
        }
        if (path == EVENT && v[2].isEmpty()) {
            String pick = pick(eventPool, r);
            if (pick != null) {
                v[2] = roomPick(r);
                v[3] = pick;
            }
        }
        return v;
    }

    static String pick(List<String> pool, Random r) {
        if (pool == null || pool.isEmpty()) return null;
        List<String> clean = new ArrayList<String>();
        for (String id : pool) if (id != null && !id.isEmpty() && !clean.contains(id)) clean.add(id);
        if (clean.isEmpty()) return null;
        return clean.get(Math.floorMod(r.nextInt(), clean.size()));
    }

    static String roomPick(Random r) {
        String[] rooms = {"r1", "r2", "r3"};
        return rooms[Math.floorMod(r.nextInt(), 3)];
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选花样委托追猎（缺则补出对应花样 · 可随时改 · 不改奖励表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[词缀追]", "/corerpg p1 bountypath affix", tip(AFFIX), "GREEN"},
                new String[]{"[事件追]", "/corerpg p1 bountypath event", tip(EVENT), "GOLD"},
                new String[]{"[双追]", "/corerpg p1 bountypath both", tip(BOTH), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 bountypath clear", "清空偏好", "DARK_GRAY"});
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

    public static void scheduleOfferAfterQ02(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 135L); // after room ~110L
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
        if (!runs.progressFlag(d, "q02")) {
            p.sendMessage(EmberRunService.P + "§c委托路径需本人首通 Q02（重打本才有花样委托）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空委托路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a委托 → §f" + label(id) + " §7· " + tip(id));
        String vb = runs.varietyBountyLine(d);
        if (vb != null && !vb.isEmpty()) p.sendMessage(P + "§7今日花样委托：§f" + vb);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "重打已通主线本推进委托（队长）", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 bountypath", "重选", "GRAY"});
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
