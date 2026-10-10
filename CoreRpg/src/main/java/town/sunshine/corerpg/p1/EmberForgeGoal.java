package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D461: weekly forge craft-goal path pick — player commits one sink path; gap reuses D404/D432 mirrors.
 * Zero AFK table / price / brand math change.
 */
public final class EmberForgeGoal {

    public static final String C_GOAL = "p1_forge_goal";
    /** Period = ISO week string; 1 = offered this week. */
    public static final String C_OFFER = "p1_forge_goal_offer";

    public static final int NONE = 0;
    public static final int ENHANCE = 1;
    public static final int REFINE = 2;
    public static final int BRAND = 3;
    public static final int ROLL = 4;
    public static final int CONVERT = 5;

    private EmberForgeGoal() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_GOAL, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == ENHANCE || id == REFINE || id == BRAND || id == ROLL || id == CONVERT;
    }

    public static boolean set(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_GOAL, "all");
        if (cur == id) return false;
        d.addPeriodCount(C_GOAL, "all", id - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("enhance".equals(s) || "强化".equals(s) || "enh".equals(s) || "1".equals(s)) return ENHANCE;
        if ("refine".equals(s) || "精工".equals(s) || "craft".equals(s) || "2".equals(s)) return REFINE;
        if ("brand".equals(s) || "烙纹".equals(s) || "3".equals(s)) return BRAND;
        if ("roll".equals(s) || "随机".equals(s) || "随机锻".equals(s) || "4".equals(s)) return ROLL;
        if ("convert".equals(s) || "转化".equals(s) || "5".equals(s)) return CONVERT;
        return -1;
    }

    public static String label(int id) {
        if (id == ENHANCE) return "强化+1";
        if (id == REFINE) return "精工+1";
        if (id == BRAND) return "合成烙纹";
        if (id == ROLL) return "随机锻造";
        if (id == CONVERT) return "每周转化";
        return "未选";
    }

    public static String tip(int id) {
        if (id == ENHANCE) return "挂机攒碎片 → 工坊强化";
        if (id == REFINE) return "挂机攒胚料/骨尘 → 工坊精工";
        if (id == BRAND) return "挂机攒碎片 → 合成烙纹";
        if (id == ROLL) return "攒胚料+币 → 随机锻一件";
        if (id == CONVERT) return "攒胚料+币 → 改族（周 1 次）";
        return "点选一条工坊路径当本周目标";
    }

    /** Gap line for the pinned goal (mirrors existing recipe/forge gap strings). */
    public static String gapLine(int id, String enhanceGap, String refineGap, String brandGap,
                                 String rollGap, String convertGap) {
        if (id == ENHANCE) {
            int g = parseIntSafe(enhanceGap);
            return g <= 0 ? "§a已够碎片 · 去工坊强化" : "§7还差碎片 §f" + g;
        }
        if (id == REFINE) {
            if (refineGap == null || refineGap.isEmpty()) return "§8—";
            return readyRefine(refineGap) ? "§a已够料 · 去工坊精工" : "§7" + refineGap;
        }
        if (id == BRAND) {
            int g = parseIntSafe(brandGap);
            return g <= 0 ? "§a已够碎片 · 去合成烙纹" : "§7还差碎片 §f" + g;
        }
        if (id == ROLL) {
            if (rollGap == null || rollGap.isEmpty()) return "§8—";
            return readyRollOrConvert(rollGap) ? "§a已够料 · 去随机锻" : "§7" + rollGap;
        }
        if (id == CONVERT) {
            if (convertGap == null || convertGap.isEmpty()) return "§8—";
            return readyRollOrConvert(convertGap) ? "§a已够料 · 去转化" : "§7" + convertGap;
        }
        return "§8未选工坊目标";
    }

    public static String glance(int id, String gap) {
        if (!valid(id)) return "§8工坊目标：未选 · /corerpg p1 forgegoal";
        return "§e工坊目标：§f" + label(id) + " §8· " + (gap == null ? "" : gap);
    }

    /** Chat path pick (real choice). */
    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选一条本周工坊路径（可随时改 · 挂机对着攒）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[强化+1]", "/corerpg p1 forgegoal enhance", tip(ENHANCE), "GOLD"},
                new String[]{"[精工+1]", "/corerpg p1 forgegoal refine", tip(REFINE), "AQUA"},
                new String[]{"[合成烙纹]", "/corerpg p1 forgegoal brand", tip(BRAND), "RED"},
                new String[]{"[随机锻]", "/corerpg p1 forgegoal roll", tip(ROLL), "LIGHT_PURPLE"},
                new String[]{"[每周转化]", "/corerpg p1 forgegoal convert", tip(CONVERT), "GREEN"});
    }

    /** Once per ISO week when forge/hub nudges. */
    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        offerPick(p);
        return true;
    }

    static int parseIntSafe(String s) {
        if (s == null || s.isEmpty()) return 0;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return 0; }
    }

    static boolean readyRefine(String gap) {
        // form 胚差N·骨差M
        return gap.contains("胚差0") && gap.contains("骨差0");
    }

    static boolean readyRollOrConvert(String gap) {
        return gap.contains("胚差0") && gap.contains("币差0");
    }
}
