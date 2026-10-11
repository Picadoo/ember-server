package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8769: first heal-potion brew of the day → coin cost ×{@link #MULT}.
 * Zero life offer table. Not Festival densify.
 */
public final class EmberBrewCoinSoft {

    public static final String C_USED = "p1_brew_coin_soft";
    public static final double MULT = 0.5;

    private EmberBrewCoinSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeCoin(PlayerData d, int coin) {
        if (coin <= 0 || !ready(d)) return coin;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(coin * MULT));
    }
}
