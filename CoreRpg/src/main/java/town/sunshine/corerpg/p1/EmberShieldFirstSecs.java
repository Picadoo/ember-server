package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7265: first signature skill_shield grant of the day +{@link #EXTRA_SECS}s.
 * Zero skill_shield_secs table / skill_mult. Not SetLand/Huohen densify.
 */
public final class EmberShieldFirstSecs {

    public static final String C_USED = "p1_shield_first_secs";
    public static final double EXTRA_SECS = 1.0;

    private EmberShieldFirstSecs() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return extra seconds (0 if not ready). Consumes day latch. */
    public static double consume(PlayerData d) {
        if (!ready(d)) return 0.0;
        d.addPeriodCount(C_USED, "day", 1);
        return EXTRA_SECS;
    }
}
