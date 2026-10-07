package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D299 · 再刷短反馈：成色 / 精工近档；D307 · 工坊菜单诚实：强化/升阶/互换/分解费用行 + 缺料半行。
 * Bukkit-free so unit tests pin the strings.
 */
public final class EmberGearNextHint {

    private EmberGearNextHint() {}

    /**
     * Compact cost mirroring {@link Cost#label()} order: shards/cores/blanks/bone/coins.
     * Refine/quality stay {@code 胚N 骨N 币N}; enhance/upgrade add {@code 碎片N 核心N}.
     */
    public static String costShort(Cost c) {
        if (c == null) return "";
        StringBuilder sb = new StringBuilder();
        if (c.shards > 0) sb.append("碎片").append(c.shards).append(' ');
        if (c.cores > 0) sb.append("核心").append(c.cores).append(' ');
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
     * D307 W1a · 强化本次费用 + 成功率/保底。满档或异常返回错误短句。
     * 形：{@code +3→+4 · 碎片12 币120 · 85%（1/3）}；保底次加「必成」。
     */
    public static String enhanceLine(EmberItemData d) {
        if (d == null) return "";
        EmberUpgradeRules.Plan c = EmberUpgradeRules.enhanceCheck(d);
        if (!c.ok()) return c.error;
        int t = d.enhance + 1;
        int pct = (int) Math.round(EmberUpgradeRules.rate(t) * 100);
        int n = EmberUpgradeRules.attemptNo(d);
        int max = EmberUpgradeRules.maxTries(t);
        String pity = EmberUpgradeRules.guaranteed(d)
                ? n + "/" + max + "必成"
                : n + "/" + max;
        return "+" + d.enhance + "→+" + t + " · " + costShort(c.cost) + " · " + pct + "%（" + pity + "）";
    }

    /**
     * D307 W1a · 升阶本次费用。闸未开时诚实「需首通 Q0x」；不可升阶时短句。
     * {@code gateOk} = 本人已首通 upgradeFlag(tier)。
     */
    public static String upgradeLine(EmberItemData d, boolean gateOk) {
        if (d == null) return "";
        Cost c = EmberUpgradeRules.upgradeCost(d.tier);
        if (c == null) {
            if (d.tier == 0) return "T0 不能升阶";
            return "已是最高阶 T" + d.tier;
        }
        String flag = EmberUpgradeRules.upgradeFlag(d.tier);
        String step = "T" + d.tier + "→T" + (d.tier + 1) + " · " + costShort(c);
        if (!gateOk) return "需首通 " + flag.toUpperCase(java.util.Locale.ROOT) + " · " + costShort(c);
        return step;
    }

    /** D307 W1a · 互换免费口径（与 ForgeService 聊天预览一致）。 */
    public static String swapLine() {
        return "免费 · 交换强化等级+失败计数";
    }

    /**
     * D307 W1a · 分解只读得胚预览。不可分解时返回检查短句；可分解 {@code 得胚×T}。
     */
    public static String dismantleYieldLine(EmberItemData d) {
        if (d == null) return "";
        String why = EmberUpgradeRules.dismantleCheck(d);
        if (why != null) return why;
        return "得胚×" + EmberUpgradeRules.dismantleYield(d);
    }

    /** D307 W1b · 缺料半行；空列表 → 空串（材料够不刷红）。口径对齐 ForgeService lacking 人话。 */
    public static String lackHalf(java.util.List<String> lack) {
        if (lack == null || lack.isEmpty()) return "";
        return "缺少：" + String.join("，", lack);
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
