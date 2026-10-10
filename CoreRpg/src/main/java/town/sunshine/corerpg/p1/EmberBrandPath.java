package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D467: brand playstyle path — hunt (猎缀) / ember (余烬纹) / bind (定身).
 * Distinct from forgegoal (activity) and set-focus (family). Zero pin price / AFK change.
 */
public final class EmberBrandPath {

    public static final String C_PATH = "p1_brand_path";
    public static final String C_OFFER = "p1_brand_path_offer";

    public static final int NONE = 0;
    public static final int HUNT = 1;   // blade b_affix
    public static final int EMBER = 2;  // blade b_set
    public static final int BIND = 3;   // charm c_tele

    private EmberBrandPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == HUNT || id == EMBER || id == BIND;
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
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("hunt".equals(s) || "猎缀".equals(s) || "b_affix".equals(s) || "1".equals(s)) return HUNT;
        if ("ember".equals(s) || "余烬".equals(s) || "余烬纹".equals(s) || "b_set".equals(s) || "2".equals(s)) return EMBER;
        if ("bind".equals(s) || "定身".equals(s) || "c_tele".equals(s) || "3".equals(s)) return BIND;
        return -1;
    }

    public static String label(int id) {
        if (id == HUNT) return "猎缀路线";
        if (id == EMBER) return "余烬路线";
        if (id == BIND) return "定身路线";
        return "未选";
    }

    public static String tip(int id) {
        if (id == HUNT) return "刃 · 猎缀纹 · 对词缀精英伤害";
        if (id == EMBER) return "刃 · 余烬纹 · 套装事件伤害";
        if (id == BIND) return "护符 · 定身纹 · 扛首领预警招";
        return "点选烙纹玩法侧移";
    }

    /** Suggested pin command after path set. */
    public static String pinCmd(int id) {
        if (id == HUNT) return "/corerpg p1 brand pin blade b_affix";
        if (id == EMBER) return "/corerpg p1 brand pin blade b_set";
        if (id == BIND) return "/corerpg p1 brand pin charm c_tele";
        return "/corerpg p1 brand menu";
    }

    public static String pinLabel(int id) {
        if (id == HUNT) return "[去定向猎缀]";
        if (id == EMBER) return "[去定向余烬]";
        if (id == BIND) return "[去定向定身]";
        return "[打开烙纹页]";
    }

    public static String glance(int id) {
        if (!valid(id)) return "§8烙纹路径：未选 · /corerpg p1 brandpath";
        return "§e烙纹路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选烙纹玩法路径（侧移打法 · 可随时改 · 不改价）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[猎缀]", "/corerpg p1 brandpath hunt", tip(HUNT), "GOLD"},
                new String[]{"[余烬]", "/corerpg p1 brandpath ember", tip(EMBER), "RED"},
                new String[]{"[定身]", "/corerpg p1 brandpath bind", tip(BIND), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 brandpath clear", "清空路径", "GRAY"});
    }

    /** Once per ISO week when unset. */
    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        offerPick(p);
        return true;
    }
}
