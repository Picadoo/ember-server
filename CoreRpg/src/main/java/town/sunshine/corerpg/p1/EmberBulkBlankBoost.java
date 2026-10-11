package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7009: bulk dismantle of {@link #NEED}+ pieces → +1 blank once per uid@day.
 * Zero dismantleYield table raise. Not Bag/sure-do densify.
 */
public final class EmberBulkBlankBoost {

    public static final String C_USED = "p1_bulk_blank_boost";
    public static final int NEED = 3;
    public static final int BONUS = 1;

    private EmberBulkBlankBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d, int pieces) {
        return d != null && pieces >= NEED && !usedToday(d);
    }

    /** @return true when bonus should be granted (consumes day latch). */
    public static boolean consume(PlayerData d, int pieces) {
        if (!ready(d, pieces)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
