package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8897: first fishing-rod life buy of the day → coin ×{@link #MULT}.
 * Zero rod offer table. Not Brew/Cook densify (tool buy).
 */
public final class EmberRodBuySoft {

    public static final String C_USED = "p1_rod_buy_soft";
    public static final double MULT = 0.5;

    private EmberRodBuySoft() {}

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
