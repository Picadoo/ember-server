package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D465: weekly set-family playstyle focus — commit scorch/burst/sustain as the set you build toward.
 * Distinct from loot {@code p1_target}. Zero set power / AFK / convert price change.
 */
public final class EmberSetFocus {

    public static final String C_FOCUS = "p1_set_focus";
    public static final String C_OFFER = "p1_set_focus_offer";

    public static final int NONE = 0;
    public static final int SCORCH = 1;
    public static final int BURST = 2;
    public static final int SUSTAIN = 3;

    private EmberSetFocus() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_FOCUS, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == SCORCH || id == BURST || id == SUSTAIN;
    }

    public static boolean set(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_FOCUS, "all");
        if (cur == id) return false;
        d.addPeriodCount(C_FOCUS, "all", id - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("scorch".equals(s) || "焚烬".equals(s) || "1".equals(s)) return SCORCH;
        if ("burst".equals(s) || "烬爆".equals(s) || "2".equals(s)) return BURST;
        if ("sustain".equals(s) || "炽愈".equals(s) || "3".equals(s)) return SUSTAIN;
        return -1;
    }

    public static String familyKey(int id) {
        if (id == SCORCH) return "scorch";
        if (id == BURST) return "burst";
        if (id == SUSTAIN) return "sustain";
        return null;
    }

    public static String label(int id) {
        String f = familyKey(id);
        return f == null ? "未选" : EmberItemData.familyName(f);
    }

    public static String tip(int id) {
        String f = familyKey(id);
        return f == null ? "点选本周主玩哪套" : EmberItemData.familyBlurb(f);
    }

    public static String glance(int id, String activeSet, int awakening, String lootTarget) {
        if (!valid(id)) return "§8套装焦点：未选 · /corerpg p1 setfocus";
        String focus = label(id);
        String wear;
        if (activeSet != null && familyKey(id).equals(activeSet))
            wear = "§a穿着中" + (awakening > 0 ? " · 觉醒" + toRoman(awakening) : "");
        else if (activeSet != null && !"none".equals(activeSet))
            wear = "§7现穿 " + EmberItemData.familyName(activeSet);
        else
            wear = "§8未成套";
        String loot = "";
        if (lootTarget != null && !lootTarget.isEmpty()) {
            loot = familyKey(id).equals(lootTarget) ? " §a· 掉落同族"
                    : " §8· 掉落 " + EmberItemData.familyName(lootTarget);
        }
        return "§e套装焦点：§f" + focus + " §8· " + wear + loot;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周套装焦点（玩法侧移 · 可随时改 · 异于掉落目标族）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[焚烬]", "/corerpg p1 setfocus scorch", tip(SCORCH), "GOLD"},
                new String[]{"[烬爆]", "/corerpg p1 setfocus burst", tip(BURST), "RED"},
                new String[]{"[炽愈]", "/corerpg p1 setfocus sustain", tip(SUSTAIN), "GREEN"},
                new String[]{"[取消]", "/corerpg p1 setfocus clear", "清空焦点", "GRAY"});
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

    static String toRoman(int n) {
        if (n == 1) return "I";
        if (n == 2) return "II";
        if (n == 3) return "III";
        return String.valueOf(n);
    }
}
