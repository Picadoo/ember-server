package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7361: first successful 燎原 transfer of the day gets +{@link #EXTRA_MS}ms burn carry.
 * Zero burnSpread table / skill_mult. Not IgniteRefresh densify.
 */
public final class EmberBurnSpreadBoost {

    public static final String C_USED = "p1_burn_spread_boost";
    public static final long EXTRA_MS = 1000L;

    private EmberBurnSpreadBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return extra ms (0 if not ready). Consumes day latch. */
    public static long consume(PlayerData d) {
        if (!ready(d)) return 0L;
        d.addPeriodCount(C_USED, "day", 1);
        return EXTRA_MS;
    }
}
