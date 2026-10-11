package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9729: first brand pin of the day → brands + coins ×{@link #MULT}.
 * Zero C21 pin tables. Not BrandSure densify.
 */
public final class EmberBrandPinCostSoft {

    public static final String C_USED = "p1_brand_pin_cost_soft";
    public static final double MULT = 0.5;

    private EmberBrandPinCostSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int[] apply(PlayerData d, int brands, int coins, boolean consume) {
        if ((brands <= 0 && coins <= 0) || !ready(d)) return new int[]{brands, coins};
        int b = brands <= 0 ? 0 : Math.max(1, (int) Math.floor(brands * MULT));
        int c = coins <= 0 ? 0 : Math.max(1, (int) Math.floor(coins * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return new int[]{b, c};
    }
}
