package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * B2.179 §19.2 item card + loadout comparison (pure text, no rule of its own). The item card follows the book order:
 * name and family, slot and tier, quality, craft, enhance, actual base stat (the §7.2 formula with the level part
 * listed separately), set condition, binding and source. The comparison is between two whole loadouts (the live one
 * and the one this item would make) so that a charm that raises HP but breaks the set says so before it is selected.
 * There is deliberately no single "power score" (§19.2).
 */
public final class EmberCompare {

    private EmberCompare() {}

    static String n(double v) {
        return Math.abs(v - Math.rint(v)) < 0.05 ? String.valueOf((long) Math.rint(v)) : String.format(Locale.ROOT, "%.1f", v);
    }

    private static String pct(double v) { return String.format(Locale.ROOT, "%.2f", v); }

    public static String sourceName(String src) {
        if ("drop".equals(src)) return "掉落（可分解）";
        if ("quest".equals(src)) return "首通 / 开局赠送（不可分解）";
        if ("reissue".equals(src)) return "补发（不可分解）";
        if ("admin".equals(src)) return "测试（不可分解）";
        if ("migrate".equals(src)) return "迁移（不可分解）";
        return src == null ? "未知" : src;
    }

    /** §19.2 item card. */
    public static List<String> card(EmberTables t, EmberItemData d, int level) {
        List<String> out = new ArrayList<String>();
        out.add("§6" + d.shortLabel());
        @SuppressWarnings("unused")         String grow = "(1+" + pct(t.enhance(d.enhance)) + "+" + pct(t.quality(d.quality)) + "+" + pct(t.craft(d.craft)) + ")";
        double g = EmberFormula.growth(t, d.quality, d.craft, d.enhance);
        if (d.isBlade()) {
            double a = t.weaponA(d.tier);
            out.add("§7攻击：§f" + n(a * g) + " §7（成长 ×" + String.format(java.util.Locale.ROOT, "%.2f", g) + "）· 余烬等级另加 §f"
                    + n(EmberFormula.levelAttack(t, level)) + "§7（Lv" + level + "）"); // D101: no raw formula
        } else if (d.isCharm()) {
            double h = t.charmH(d.tier);
            double def = EmberFormula.defense(t, d.tier);
            out.add("§7生命：§f" + n(20 + h * g) + " §7（成长 ×" + String.format(java.util.Locale.ROOT, "%.2f", g) + "）· 余烬等级另加 §f"
                    + n(EmberFormula.levelHp(t, level)) + "§7（Lv" + level + "）");
            out.add("§7防御：§f" + n(def) + "§7（承伤 ×" + pct(EmberFormula.mitigation(t, def)) + "，强化 / 成色 / 精工不改防御）");
        }
        if ("none".equals(d.family) || d.tier < 1) out.add("§7套装：§8T0 / 无族，不组成套装");
        else out.add("§7套装：§f主手刃 + 选定护符同为" + EmberItemData.familyName(d.family)
                + "且都 ≥ T1 时启用 " + EmberItemData.familyName(d.family) + "；两件都 T2+6 觉醒II，都 T3+9 觉醒III");
        out.add("§7" + (d.bound ? "绑定" : "未绑定") + " · 来源：" + sourceName(d.source));
        return out;
    }

    private static String arrow(double a, double b) {
        if (Math.abs(a - b) < 0.05) return "§f" + n(a) + " §8(不变)";
        return "§f" + n(a) + " → " + (b > a ? "§a" : "§c") + n(b);
    }

    /** Whole-loadout comparison: what changes if {@code after} replaces {@code before}. */
    public static List<String> diff(EmberLoadout before, EmberLoadout after) {
        List<String> out = new ArrayList<String>();
        out.add("§7基准攻击 B：" + arrow(before.b, after.b));
        out.add("§7最大生命：" + arrow(before.h, after.h) + " §7· 防御：" + arrow(before.d, after.d));
        out.add("§7有效生命：" + arrow(before.ehp(), after.ehp()));
        String s0 = before.setLabel(), s1 = after.setLabel();
        if (s0.equals(s1)) out.add("§7套装：§f" + s1 + " §8(不变)");
        else {
            boolean lost = !"none".equals(before.activeSet) && !before.activeSet.equals(after.activeSet);
            boolean down = before.activeSet.equals(after.activeSet) && after.awakening < before.awakening;
            out.add("§7套装：§f" + s0 + " → " + (lost || down ? "§c" : "§a") + s1
                    + (lost ? " §c（会取消" + EmberItemData.familyName(before.activeSet) + "）" : down ? " §c（觉醒降档）" : ""));
        }
        return out;
    }
}
