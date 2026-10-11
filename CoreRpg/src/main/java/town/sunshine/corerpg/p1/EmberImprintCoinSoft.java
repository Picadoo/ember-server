package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9793: first imprint of the day → coin ×{@link #MULT}.
 * Zero imprintMarks / STAMP_RATE. Marks cost unchanged.
 */
public final class EmberImprintCoinSoft {

    public static final String C_USED = "p1_imprint_coin_soft";
    public static final double MULT = 0.5;

    private EmberImprintCoinSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int coin, boolean consume) {
        if (coin <= 0 || !ready(d)) return coin;
        int soft = Math.max(1, (int) Math.floor(coin * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
