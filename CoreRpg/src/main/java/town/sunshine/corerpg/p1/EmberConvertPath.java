package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D469: weekly convert destination-family path — commit scorch/burst/sustain for convert.
 * Distinct from set-focus (wear) and loot target. Zero convert price / AFK change.
 */
public final class EmberConvertPath {

    public static final String C_PATH = "p1_convert_path";
    public static final String C_OFFER = "p1_convert_path_offer";

    public static final int NONE = 0;
    public static final int SCORCH = 1;
    public static final int BURST = 2;
    public static final int SUSTAIN = 3;

    private EmberConvertPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == SCORCH || id == BURST || id == SUSTAIN;
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
        if (f == null) return "点选本周转化目标族";
        return "手持刃/护符 → 转化成" + EmberItemData.familyName(f) + "（周 1 次）";
    }

    public static String glance(int id, String setFocusFam, String lootTarget) {
        if (!valid(id)) return "§8转化路径：未选 · /corerpg p1 convertpath";
        String focus = label(id);
        String extra = "";
        String fk = familyKey(id);
        if (fk != null && setFocusFam != null && fk.equals(setFocusFam))
            extra += " §a· 套装焦点同族";
        else if (setFocusFam != null && !setFocusFam.isEmpty())
            extra += " §8· 焦点 " + EmberItemData.familyName(setFocusFam);
        if (fk != null && lootTarget != null && fk.equals(lootTarget))
            extra += " §a· 掉落同族";
        return "§e转化路径：§f" + focus + extra;
    }

    
    /** D732 / TIP-AW D12863: week convert remain line — CapSoft ready → 容错待用. */
    public static String weekLineFor(int used, boolean capSoftReady) {
        int left = Math.max(0, EmberConvertRules.WEEKLY_CAP - used);
        if (left > 0)
            return "§e本周转化剩余：§f" + left + "/" + EmberConvertRules.WEEKLY_CAP;
        if (capSoftReady)
            return "§e本周转化 · 周次容错待用 · 还可 1 次";
        return "§a本周转化环已完成 §8· 下周重置";
    }

    /** D732: week convert remain line. */
    public static String weekLine(PlayerData d) {
        if (d == null) return "§8本周转化：—";
        String week = town.sunshine.corerpg.DailyService.weekId();
        int used = d.periodCount(EmberConvertRules.C_CONV, week);
        return weekLineFor(used, EmberConvertCapSoft.ready(d));
    }

    /** TIP-AW: preview message fragment after · (no leading §7). */
    public static String weekRemainFrag(int used, boolean capSoftReady) {
        int left = Math.max(0, EmberConvertRules.WEEKLY_CAP - used);
        if (left > 0)
            return "本周剩余 §f" + left + "/" + EmberConvertRules.WEEKLY_CAP;
        if (capSoftReady)
            return "本周 · §e容错待用 · 还可 1 次";
        return "本周剩余 §f0/" + EmberConvertRules.WEEKLY_CAP;
    }

    /** TIP-AW: ActionBar week-remain short. */
    public static String weekRemainAb(int used, boolean capSoftReady) {
        int left = Math.max(0, EmberConvertRules.WEEKLY_CAP - used);
        if (left > 0)
            return "§e转化 · 周余 §f" + left;
        if (capSoftReady)
            return "§e转化 · 容错待用 · 还可 1 次";
        return "§e转化 · 周余 §f0";
    }

    /** TIP-AW: afterSet ActionBar remain. */
    public static String weekRemainAbAfterSet(int used, boolean capSoftReady) {
        int left = Math.max(0, EmberConvertRules.WEEKLY_CAP - used);
        if (left > 0)
            return "§e转化环 · 余 §f" + left;
        if (capSoftReady)
            return "§e转化环 · 容错待用 · 还可 1 次";
        return "§e转化环 · 余 §f0";
    }


    /** D728/D887: after convertpath set — glance + week remain + next buttons. */
    public static void afterSet(Player p, PlayerData d, int id, boolean changed, int setFocusId, String lootTarget) {
        if (p == null || !valid(id)) return;
        String P = EmberRunService.P;
        String fk = familyKey(id);
        p.sendMessage(P + "§a转化路径 → §f" + label(id) + "§7（" + tip(id) + "）"
                + (changed ? "" : " §8· 已是该路径"));
        p.sendMessage(P + glance(id, EmberSetFocus.familyKey(setFocusId), lootTarget));
        p.sendMessage(P + weekLine(d));
        int used = 0;
        boolean softReady = false;
        if (d != null) {
            String week = town.sunshine.corerpg.DailyService.weekId();
            used = d.periodCount(EmberConvertRules.C_CONV, week);
            softReady = EmberConvertCapSoft.ready(d);
        }
        try { p.sendActionBar(weekRemainAbAfterSet(used, softReady)); } catch (Throwable ignored) { }
        ConfirmTokens.sendButtons(p, P + "§7转化环：", // D887
                new String[]{"[去转化成" + label(id) + "]", "/corerpg p1 convert " + fk,
                        "手持刃/护符后确认（周 1 次）", "GREEN"},
                new String[]{"[图装追]", "/corerpg p1 mapfarm", "刷同族料/件", "AQUA"},
                new String[]{"[工坊]", "/corerpg p1 forge", "转化页在工坊", "GOLD"});
    }

public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周转化目标族（周 1 次跨族 · 可随时改 · 异于套装焦点/掉落）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[焚烬]", "/corerpg p1 convertpath scorch", tip(SCORCH), "GOLD"},
                new String[]{"[烬爆]", "/corerpg p1 convertpath burst", tip(BURST), "RED"},
                new String[]{"[炽愈]", "/corerpg p1 convertpath sustain", tip(SUSTAIN), "GREEN"},
                new String[]{"[取消]", "/corerpg p1 convertpath clear", "清空路径", "GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        offerPick(p);
        return true;
    }
}
