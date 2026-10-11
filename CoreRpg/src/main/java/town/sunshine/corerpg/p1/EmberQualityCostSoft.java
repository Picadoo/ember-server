package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D9921: first quality forge of the day → cost ×{@link #MULT}.
 * Zero C06 table. Not Enhance/Upgrade/RefineCost densify (quality op).
 */
public final class EmberQualityCostSoft {

    public static final String C_USED = "p1_quality_cost_soft";
    public static final double MULT = 0.5;

    private EmberQualityCostSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static Cost apply(PlayerData d, Cost c, boolean consume) {
        if (c == null || c.equals(Cost.NONE) || !ready(d)) return c;
        Cost soft = new Cost(h(c.shards), h(c.cores), h(c.blanks), h(c.bone), h(c.coins));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }

    static int h(int n) {
        if (n <= 0) return 0;
        return Math.max(1, (int) Math.floor(n * MULT));
    }
}
