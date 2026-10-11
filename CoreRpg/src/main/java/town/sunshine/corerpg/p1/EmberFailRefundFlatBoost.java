package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10753: first positive fail-stamina refund of the day → +{@link #BONUS} stamina.
 * Zero fail_refund table. Not FailPath densify (flat add, not share path).
 */
public final class EmberFailRefundFlatBoost {

    public static final String C_USED = "p1_fail_refund_flat_boost";
    public static final int BONUS = 5;

    private EmberFailRefundFlatBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int back) {
        if (back <= 0 || !ready(d)) return back;
        d.addPeriodCount(C_USED, "day", 1);
        return back + BONUS;
    }
}
