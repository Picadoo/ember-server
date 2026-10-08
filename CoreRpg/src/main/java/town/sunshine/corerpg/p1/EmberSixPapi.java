package town.sunshine.corerpg.p1;

import java.util.List;

/**
 * D318 六槽 T1-8 · {@code %corerpg_p1_armor_*%} texts for {@code ember_p1_armor} / the gear page (Bukkit-free; the
 * numbers come from {@link EmberSixRank}, i.e. the real formula). Switch off → every key is "" except {@code armor_on} = 0
 * and the 待领 keys (D320 ④: claim works with the switch off, so the menu can still show it).
 *
 * <pre>
 * armor_on                     1 / 0 (menu shows the 护甲 button only when 1)
 * armor_&lt;slot&gt;                 worn: "§f胸甲 · 灰烬族" / "§8空（按成色标准、精工 0% 计算）"
 * armor_&lt;slot&gt;_info            "§7成色 卓越 · 精工 4%" + " · §8掉落阶 T2"
 * armor_&lt;slot&gt;_has             1 / 0 (a backpack candidate exists)
 * armor_&lt;slot&gt;_cand            "§f背包里：胸甲 · 灰烬族" / "§8背包里没有同部位的护甲"
 * armor_&lt;slot&gt;_cmp             "§7成色 精良 → 卓越 · 精工 2% → 4%"
 * armor_&lt;slot&gt;_delta           "§a生命 +0.5" / "§7生命 不变" / "§c生命 -0.3"
 * armor_&lt;slot&gt;_fam             set-progress change on equip (D169 /2); empty when unchanged
 * armor_set                    D169 progress / active line
 * armor_set_active             1 / 0 (four-piece active)
 * armor_set_busy               1 / 0 (two-piece on, four-piece not yet)
 * armor_all                    "§7将换上 2 件 · 生命 +1.2" / "§7四个部位都已是最好的一件"
 * armor_all_has                1 / 0
 * armor_stash                  待领 count (also when off)
 * armor_stash_has              1 / 0 (also when off: the 待领 entry shows only when there is something to claim)
 * armor_stash_line             "§7护甲位原来的 2 件已存好 · §e点这里领取" / "§7没有待领物品" (also when off)
 * </pre>
 */
public final class EmberSixPapi {

    private EmberSixPapi() {}

    static final String[] SLOT_KEYS = {"head", "chest", "legs", "boots"};

    public static String text(boolean enabled, EmberSixRank.View v, int stash, String key) {
        if ("armor_on".equals(key)) return enabled ? "1" : "0";
        if ("armor_stash".equals(key)) return String.valueOf(Math.max(0, stash));
        if ("armor_stash_has".equals(key)) return stash > 0 ? "1" : "0";
        if ("armor_stash_line".equals(key)) return stash > 0 ? stashLine(stash) : "§7没有待领物品";
        if (!enabled || v == null) return "";
        if ("armor_set_active".equals(key)) {
            Object[] s = EmberSixRank.setProgress(v.blade, v.charm, v.worn);
            return Boolean.TRUE.equals(s[3]) ? "1" : "0";
        }
        if ("armor_set_busy".equals(key)) {
            Object[] s = EmberSixRank.setProgress(v.blade, v.charm, v.worn);
            String fam = (String) s[0];
            return fam != null && !fam.isEmpty() && !Boolean.TRUE.equals(s[3]) ? "1" : "0";
        }
        if ("armor_set".equals(key)) {
            Object[] s = EmberSixRank.setProgress(v.blade, v.charm, v.worn);
            String fam = (String) s[0];
            int n = ((Integer) s[1]).intValue();
            if (fam == null || fam.isEmpty()) return "§7先让刃与护符同族";
            if (Boolean.TRUE.equals(s[3]))
                return "§a" + EmberItemData.familyName(fam) + "族 护甲 " + n + "/2 · 受伤 −3%";
            return "§e" + EmberItemData.familyName(fam) + "族 护甲 " + n + "/2 · 需同族掉落阶 T2+";
        }
        if ("armor_all".equals(key) || "armor_all_has".equals(key)) {
            List<Integer> todo = EmberSixRank.allPlan(v);
            if ("armor_all_has".equals(key)) return todo.isEmpty() ? "0" : "1";
            return todo.isEmpty() ? "§7四个部位都已是最好的一件" : "§7将换上 " + todo.size() + " 件 · " + EmberSixRank.deltaText(EmberSixRank.allDelta(v, todo));
        }
        if (!key.startsWith("armor_")) return "";
        String rest = key.substring(6);
        int us = rest.indexOf('_');
        String slotKey = us < 0 ? rest : rest.substring(0, us);
        String field = us < 0 ? "" : rest.substring(us + 1);
        int i = -1;
        for (int k = 0; k < 4; k++) if (SLOT_KEYS[k].equals(slotKey)) i = k;
        if (i < 0) return "";
        EmberItemData w = v.worn[i];
        EmberSixRank.Pick c = v.best[i];
        String lab = EmberSixSlot.slotLabel(i);
        switch (field) {
            case "": return w == null ? "§8" + lab + "：空（按成色标准、精工 0% 计算）" : "§f" + lab + " · " + EmberItemData.familyName(w.family) + "族";
            case "info": return w == null ? "" : "§7成色 " + EmberItemData.qualityName(w.quality) + " · 精工 " + (w.craft * 2) + "% · §8掉落阶 T" + w.tier;
            case "has": return c == null ? "0" : "1";
            case "cand": return c == null ? "§8背包里没有同部位的护甲" : "§f背包里：" + lab + " · " + EmberItemData.familyName(c.piece.family) + "族";
            case "cmp":
                if (c == null) return "";
                int q0 = w == null ? 0 : w.quality, f0 = w == null ? 0 : w.craft;
                return "§7成色 " + EmberItemData.qualityName(q0) + " → " + EmberItemData.qualityName(c.piece.quality)
                        + " · 精工 " + (f0 * 2) + "% → " + (c.piece.craft * 2) + "%";
            case "delta":
                if (c == null) return "";
                return (c.delta > 0 ? "§a" : c.delta < 0 ? "§c" : "§7") + EmberSixRank.deltaText(c.delta);
            case "fam": {
                if (c == null) return "";
                Object[] before = EmberSixRank.setProgress(v.blade, v.charm, v.worn);
                EmberItemData[] afterWorn = v.worn.clone();
                afterWorn[i] = c.piece;
                Object[] aft = EmberSixRank.setProgress(v.blade, v.charm, afterWorn);
                int b = ((Integer) before[1]).intValue(), a = ((Integer) aft[1]).intValue();
                boolean ba = Boolean.TRUE.equals(before[3]), aa = Boolean.TRUE.equals(aft[3]);
                if (b == a && ba == aa) return ""; // 族不变且进度不变：不刷套装行
                if (ba && !aa) return "§c四件套将中断（护甲 " + b + "/2 → " + a + "/2）";
                if (!ba && aa) return "§e四件套 护甲 " + b + "/2 → " + a + "/2（可激活）";
                return "§e四件套 护甲 " + b + "/2 → " + a + "/2";
            }
            default: return "";
        }
    }

    /** forge page lines while holding armor: K0 refusal for every track; dismantle shows the 0.1 × tier tenths */
    public static String heldArmorLine(EmberItemData d, String kind) {
        if ("dismantle".equals(kind)) {
            int tenths = EmberUpgradeRules.armorDismantleTenths(d);
            if (EmberUpgradeRules.dismantleCheck(d) != null || tenths <= 0) return "§8这件护甲不能分解";
            return "§7分解得白板胚 " + (tenths / 10) + "." + (tenths % 10) + " 个（零头攒满 1 个自动到账）";
        }
        return "§8" + EmberUpgradeRules.ARMOR_REFUSE;
    }

    // ------------------------------------------------------------------ D320 ⑤ menu clicks: every click gets a reply

    public static final String OFF_TEXT = "护甲功能尚未开放。";

    public enum Route { CLAIM, EQUIP, ALL, WORN, SET, OFF, UNKNOWN }

    /**
     * what a click on ember_p1_armor does. Off: 待领 claim only while there is something to claim (D320 ④), every other
     * button → OFF ({@link #OFF_TEXT}). On: the five page actions; anything else → UNKNOWN (the service still replies).
     */
    public static Route route(boolean enabled, int stash, String a) {
        if (!enabled) return "claim".equals(a) && stash > 0 ? Route.CLAIM : Route.OFF;
        if ("claim".equals(a)) return Route.CLAIM;
        if ("equip".equals(a)) return Route.EQUIP;
        if ("all".equals(a)) return Route.ALL;
        if ("worn".equals(a)) return Route.WORN;
        if ("set".equals(a)) return Route.SET;
        return Route.UNKNOWN;
    }

    /** click on a worn slot (read-only icon): what is worn there and how to change it */
    public static String wornReply(EmberSixRank.View v, int slot) {
        if (v == null || slot < 0 || slot > 3) return "请从护甲页点选部位。";
        String lab = EmberSixSlot.slotLabel(slot);
        EmberItemData w = v.worn[slot];
        String how = v.best[slot] == null ? "背包里暂时没有可换的" + lab + "。" : "下面一格是背包里最好的" + lab + "，点它就能换上。";
        if (w == null) return lab + "位空着（按成色标准、精工 0% 计算）。" + how + "也可以直接把" + lab + "拖进护甲栏。";
        return "穿着" + lab + "：" + EmberItemData.familyName(w.family) + "族 · 成色 " + EmberItemData.qualityName(w.quality)
                + " · 精工 " + (w.craft * 2) + "%。" + how;
    }

    /** click on the 四件套 icon: D169 progress + Stage2 C effect copy when active */
    public static String setReply(EmberSixRank.View v) {
        if (v == null) return "四件套未激活：先让刃与护符同族。";
        Object[] s = EmberSixRank.setProgress(v.blade, v.charm, v.worn);
        String fam = (String) s[0];
        int n = ((Integer) s[1]).intValue();
        if (fam == null || fam.isEmpty()) return "四件套未激活：先让刃与护符同族。";
        String name = EmberItemData.familyName(fam);
        if (Boolean.TRUE.equals(s[3]))
            return "四件套已激活（" + name + "族护甲 " + n + "/2）：受伤略减（−3%）。另 2 甲位可穿异族追成色。";
        return "四件套进行中：" + name + "族护甲 " + n + "/2，再凑同族掉落阶 T2+ 的护甲即可激活。";
    }

    /** spec §5.4-1 player copy for items waiting in 待领 */
    public static String stashLine(int n) { return "§7护甲位原来的 " + n + " 件已存好 · §e点这里领取"; }
}
