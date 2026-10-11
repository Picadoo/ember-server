package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7521: successful dup-mode reroll → refund {@link #BONUS} blank once per uid@day.
 * Zero dismantleYield / reroll price table. Not BulkBlank densify.
 */
public final class EmberRerollDupRefund {

    public static final String C_USED = "p1_reroll_dup_refund";
    public static final int BONUS = 1;

    private EmberRerollDupRefund() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return true when bonus should be granted (consumes day latch). */
    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
