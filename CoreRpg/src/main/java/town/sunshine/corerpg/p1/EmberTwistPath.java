package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D504: elite-twist play path — primary / alt / rotate.
 * NEW combat feel: which Extra.ELITE light move(s) fire (D182/D185).
 * Party-leader sticky at elite spawn. Chat only. Zero dmg table / AFK / sx / forge.
 * Not combo sticky-path, not roll-bias, not prep/short twin, not enter/ActionBar cue.
 */
public final class EmberTwistPath {

    public static final String C_PATH = "p1_twist_path";
    public static final String C_OFFER = "p1_twist_path_offer";

    public static final int NONE = 0;
    /** Primary twist only (disable Pack 2 alt) */
    public static final int PRIM = 1;
    /** Alt twist only when present; else primary */
    public static final int ALT = 2;
    /** Alternate primary ↔ alt (stock D185) */
    public static final int ROTATE = 3;

    private EmberTwistPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == PRIM || id == ALT || id == ROTATE;
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
        if ("prim".equals(s) || "primary".equals(s) || "固招".equals(s) || "主招".equals(s) || "1".equals(s)) return PRIM;
        if ("alt".equals(s) || "变招".equals(s) || "次招".equals(s) || "2".equals(s)) return ALT;
        if ("rotate".equals(s) || "both".equals(s) || "轮换".equals(s) || "3".equals(s)) return ROTATE;
        return -1;
    }

    public static String key(int id) {
        if (id == PRIM) return "prim";
        if (id == ALT) return "alt";
        if (id == ROTATE) return "rotate";
        return "none";
    }

    public static String label(int id) {
        if (id == PRIM) return "精英·固招";
        if (id == ALT) return "精英·变招";
        if (id == ROTATE) return "精英·轮换";
        return "未选";
    }

    public static String tip(int id) {
        if (id == PRIM) return "奖励精英只放主变招 · 招式更可读";
        if (id == ALT) return "奖励精英优先/只放第二变招 · 换手感";
        if (id == ROTATE) return "奖励精英主招↔变招轮换 · 默认节奏";
        return "点选奖励精英变招打法";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8变招路径：首通 Q01 后可选";
        if (!valid(id)) return "§8变招路径：未选 · /corerpg p1 twistpath";
        return "§e变招路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Bukkit-free: pick which skills to attach. Returns {primary, altOrNull}.
     * NONE → stock rotate (both when alt exists).
     */
    public static EmberRunMaps.Skill[] applySkills(EmberRunMaps.EliteTwists.Twist tw, double atk, int path) {
        if (tw == null) return new EmberRunMaps.Skill[]{null, null};
        EmberRunMaps.Skill prim = tw.skill(atk);
        EmberRunMaps.Skill alt = tw.alt != null ? tw.alt.skill(atk) : null;
        if (path == PRIM) return new EmberRunMaps.Skill[]{prim, null};
        if (path == ALT) {
            if (alt != null) return new EmberRunMaps.Skill[]{alt, null};
            return new EmberRunMaps.Skill[]{prim, null};
        }
        // ROTATE or NONE → both (stock)
        return new EmberRunMaps.Skill[]{prim, alt};
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选奖励精英变招（改本局精英出招 · 可随时改 · 不改伤害表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[固招]", "/corerpg p1 twistpath prim", tip(PRIM), "GREEN"},
                new String[]{"[变招]", "/corerpg p1 twistpath alt", tip(ALT), "GOLD"},
                new String[]{"[轮换]", "/corerpg p1 twistpath rotate", tip(ROTATE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 twistpath clear", "清空偏好", "DARK_GRAY"});
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

    /** After Q01 — delay past shortpath (190L). */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 215L);
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
            p.sendMessage(EmberRunService.P + "§c变招路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空变招路径（精英按默认轮换）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a变招 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "进本遇奖励精英时生效（队长路径）", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 twistpath", "重选", "GRAY"});
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
