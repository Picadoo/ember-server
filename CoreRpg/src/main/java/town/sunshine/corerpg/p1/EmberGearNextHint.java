package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D299 · 再刷短反馈方案 M：成色 / 精工近档文案（装备页 / 工坊只读）+ 结算相对穿着对照。
 * Bukkit-free so unit tests pin the strings.
 */
public final class EmberGearNextHint {

    private EmberGearNextHint() {}

    /** Compact cost: {@code 胚16 骨16 币1600} (matches design). */
    public static String costShort(Cost c) {
        if (c == null) return "";
        StringBuilder sb = new StringBuilder();
        if (c.blanks > 0) sb.append("胚").append(c.blanks).append(' ');
        if (c.bone > 0) sb.append("骨").append(c.bone).append(' ');
        if (c.coins > 0) sb.append("币").append(c.coins);
        String s = sb.toString().trim();
        return s.isEmpty() ? "免费" : s;
    }

    public static String craftPct(int craft) {
        return (Math.max(0, craft) * 2) + "%";
    }

    /**
     * 成色近档一行。{@code null} piece → empty (caller shows「无穿着」).
     * q0→1 / 1→2：下一档 + 费用；q2：极品仅掉落；q3：已满。
     */
    public static String qualityLine(EmberItemData d) {
        if (d == null) return "";
        String cur = EmberItemData.qualityName(d.quality);
        Cost c = EmberUpgradeRules.qualityCost(d.quality);
        if (c != null && d.quality < 2) {
            return "成色 " + cur + " → " + EmberItemData.qualityName(d.quality + 1) + "（" + costShort(c) + "）";
        }
        if (d.quality == 2) return "成色 " + cur + " → 极品仅掉落";
        if (d.quality >= 3) return "成色 " + cur + " · 已满";
        return "成色 " + cur;
    }

    /**
     * 精工近档一行。满档「已满」；否则当前% → 下一档% + 费用。
     */
    public static String craftLine(EmberItemData d) {
        if (d == null) return "";
        Cost c = EmberUpgradeRules.refineCost(d.craft);
        if (c == null) return "精工 " + craftPct(d.craft) + " · 已满";
        return "精工 " + craftPct(d.craft) + " → " + craftPct(d.craft + 1) + "（" + costShort(c) + "）";
    }

    /**
     * W1c：掉落件相对同部位穿着。无穿着 / 成色精工都无变化 → {@code null}（静默）。
     * 否则 {@code 相对穿着：成色↑ · 精工↑} / {@code 相对穿着：低于} 等。
     */
    public static String relativeLine(EmberItemData fresh, EmberItemData worn) {
        if (fresh == null || worn == null) return null;
        if (fresh.slot == null || !fresh.slot.equals(worn.slot)) return null;
        return relativeStats(fresh.quality, fresh.craft, worn);
    }

    /** Same as {@link #relativeLine} but from grant roll fields (no full EmberItemData yet). */
    public static String relativeStats(int freshQ, int freshC, EmberItemData worn) {
        if (worn == null) return null;
        int dq = Integer.compare(freshQ, worn.quality);
        int dc = Integer.compare(freshC, worn.craft);
        if (dq == 0 && dc == 0) return null; // 相对无变化 → 静默
        if (dq > 0 || dc > 0) {
            StringBuilder sb = new StringBuilder("相对穿着：");
            boolean first = true;
            if (dq > 0) { sb.append("成色↑"); first = false; }
            else if (dq < 0) { sb.append("成色↓"); first = false; }
            if (dc > 0) { if (!first) sb.append(" · "); sb.append("精工↑"); }
            else if (dc < 0) { if (!first) sb.append(" · "); sb.append("精工↓"); }
            return sb.toString();
        }
        return "相对穿着：低于";
    }
}
