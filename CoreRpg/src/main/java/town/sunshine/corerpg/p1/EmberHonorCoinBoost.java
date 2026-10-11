package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10113: first positive honor-coin settle of the day → +{@link #BONUS} coin.
 * Zero honor table. Not MainlineCoin/RaidCoin densify (honor overlay).
 */
public final class EmberHonorCoinBoost {

    public static final String C_USED = "p1_honor_coin_boost";
    public static final int BONUS = 5;

    private EmberHonorCoinBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int honorCoin) {
        if (honorCoin <= 0 || !ready(d)) return honorCoin;
        d.addPeriodCount(C_USED, "day", 1);
        return honorCoin + BONUS;
    }
}
