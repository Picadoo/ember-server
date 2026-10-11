package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9761: first convert of the day → blanks + coins ×{@link #MULT}.
 * Zero C23 tables. Not ConvertCapSoft densify (cost, not cap).
 */
public final class EmberConvertCostSoft {

    public static final String C_USED = "p1_convert_cost_soft";
    public static final double MULT = 0.5;

    private EmberConvertCostSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int[] apply(PlayerData d, int blanks, int coins, boolean consume) {
        if ((blanks <= 0 && coins <= 0) || !ready(d)) return new int[]{blanks, coins};
        int b = blanks <= 0 ? 0 : Math.max(1, (int) Math.floor(blanks * MULT));
        int c = coins <= 0 ? 0 : Math.max(1, (int) Math.floor(coins * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return new int[]{b, c};
    }
}
