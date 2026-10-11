package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9633: first non-armor dismantle of the day → +{@link #BONUS} blank.
 * Zero dismantleYield table. Not forge-cost densify.
 */
public final class EmberDismantleYieldBoost {

    public static final String C_USED = "p1_dismantle_yield_boost";
    public static final int BONUS = 1;

    private EmberDismantleYieldBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Preview peek or pay consume. Returns blanks after bonus. */
    public static int apply(PlayerData d, int blanks, boolean consume) {
        if (blanks <= 0 || !ready(d)) return blanks;
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return blanks + BONUS;
    }
}
