package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7041: first 焚烬续燃 of the day extends burn end by {@link #EXTRA_TICKS} tick intervals.
 * Zero BURN_TICKS / skill_mult table raise. Not PostureGeom densify.
 */
public final class EmberIgniteRefreshBonus {

    public static final String C_USED = "p1_ignite_refresh_bonus";
    public static final int EXTRA_TICKS = 1;

    private EmberIgniteRefreshBonus() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return extra tick count to add (0 if not ready). Consumes day latch. */
    public static int consumeExtraTicks(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return EXTRA_TICKS;
    }
}
