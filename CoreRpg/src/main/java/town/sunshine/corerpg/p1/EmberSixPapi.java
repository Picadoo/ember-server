package town.sunshine.corerpg.p1;

import java.util.List;

/**
 * D318 六槽 T1-8 · {@code %corerpg_p1_armor_*%} texts for {@code ember_p1_armor} / the gear page (Bukkit-free; the
 * numbers come from {@link EmberSixRank}, i.e. the real formula). Switch off → every key is "" except {@code armor_on} = 0.
 *
 * <pre>
 * armor_on                     1 / 0 (menu shows the 护甲 button only when 1)
 * armor_&lt;slot&gt;                 worn: "§f胸甲 · 灰烬族" / "§8空（按成色标准、精工 0% 计算）"
 * armor_&lt;slot&gt;_info            "§7成色 卓越 · 精工 4%" + " · §8掉落阶 T2"
 * armor_&lt;slot&gt;_has             1 / 0 (a backpack candidate exists)
 * armor_&lt;slot&gt;_cand            "§f背包里：胸甲 · 灰烬族" / "§8背包里没有同部位的护甲"
 * armor_&lt;slot&gt;_cmp             "§7成色 精良 → 卓越 · 精工 2% → 4%"
 * armor_&lt;slot&gt;_delta           "§a生命 +0.5" / "§7生命 不变" / "§c生命 -0.3"
 * armor_&lt;slot&gt;_fam             "§e族不同：四件套 2/4 → 1/4" (only when the family changes)
 * armor_set                    "§f灰烬族 3/4 · 需掉落阶 T2+" / "§7还没有穿带族的护甲"
 * armor_all                    "§7将换上 2 件 · 生命 +1.2" / "§7四个部位都已是最好的一件"
 * armor_all_has                1 / 0
 * armor_stash                  待领 count
 * armor_stash_line             "§7护甲位原来的 2 件已存好 · §e点这里领取" / "§7没有待领物品"
 * </pre>
 */
public final class EmberSixPapi {

    private EmberSixPapi() {}

    static final String[] SLOT_KEYS = {"head", "chest", "legs", "boots"};

    public static String text(boolean enabled, EmberSixRank.View v, int stash, String key) {
        if ("armor_on".equals(key)) return enabled ? "1" : "0";
        if (!enabled || v == null) return "";
        if ("armor_stash".equals(key)) return String.valueOf(stash);
        if ("armor_stash_line".equals(key)) return stash > 0 ? stashLine(stash) : "§7没有待领物品";
        if ("armor_set".equals(key)) {
            Object[] s = EmberSixRank.setProgress(v.worn);
            return s == null ? "§7还没有穿带族的护甲" : "§f" + EmberItemData.familyName((String) s[0]) + "族 " + s[1] + "/4 · 需掉落阶 T" + s[2] + "+";
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
                if (c == null || w != null && w.family.equals(c.piece.family)) return "";
                Object[] before = EmberSixRank.setProgress(v.worn);
                EmberItemData[] after = v.worn.clone();
                after[i] = c.piece;
                Object[] aft = EmberSixRank.setProgress(after);
                int b = before == null ? 0 : (Integer) before[1], a = aft == null ? 0 : (Integer) aft[1];
                String fb = before == null ? "" : EmberItemData.familyName((String) before[0]) + "族 ";
                return "§e族不同：四件套 " + fb + b + "/4 → " + (aft == null ? "" : EmberItemData.familyName((String) aft[0]) + "族 ") + a + "/4";
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

    /** spec §5.4-1 player copy for items waiting in 待领 */
    public static String stashLine(int n) { return "§7护甲位原来的 " + n + " 件已存好 · §e点这里领取"; }
}
