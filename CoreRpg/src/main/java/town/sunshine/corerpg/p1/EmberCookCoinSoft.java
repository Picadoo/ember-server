package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8849: first cook batch of the day → fee ×{@link #MULT}.
 * Zero cook.coin_each table. Not BrewCoin densify (cook, not brew).
 */
public final class EmberCookCoinSoft {

    public static final String C_USED = "p1_cook_coin_soft";
    public static final double MULT = 0.5;

    private EmberCookCoinSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeFee(PlayerData d, int fee) {
        if (fee <= 0 || !ready(d)) return fee;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(fee * MULT));
    }
}
