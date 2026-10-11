package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9681: first death-refund of the day → refund max +{@link #BONUS}.
 * Zero S15 / death_refund.max_potions tables. Not RefundPath densify.
 */
public final class EmberDeathRefundBoost {

    public static final String C_USED = "p1_death_refund_boost";
    public static final int BONUS = 2;

    private EmberDeathRefundBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int applyMax(PlayerData d, int max) {
        if (max <= 0 || !ready(d)) return max;
        d.addPeriodCount(C_USED, "day", 1);
        return max + BONUS;
    }
}
