package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D6881: 烬斩蓄力落空 → restore half shared CD once per uid@day.
 * Zero base skill CD / skill_mult. Not DashStep densify.
 */
public final class EmberChargeMissRest {

    public static final String C_USED = "p1_charge_miss_rest";
    /** fraction of full CD to restore (0.5 = half). */
    public static final double REFUND_FRAC = 0.5;

    private EmberChargeMissRest() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return ms to subtract from skillCdUntil (0 if not ready). Consumes day latch. */
    public static long consumeRefundMs(PlayerData d, int cdSeconds) {
        if (!ready(d) || cdSeconds <= 0) return 0L;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.round(cdSeconds * 1000L * REFUND_FRAC);
    }
}
