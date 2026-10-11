package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7233: first 火痕步 ignite of the day +{@link #EXTRA} radius.
 * Zero stepIgniteRadius base / skill_mult. Not IgniteRefresh densify.
 */
public final class EmberHuohenRadiusBoost {

    public static final String C_USED = "p1_huohen_radius_boost";
    public static final double EXTRA = 0.35;

    private EmberHuohenRadiusBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return extra radius (0 if not ready). Consumes day latch. */
    public static double consume(PlayerData d) {
        if (!ready(d)) return 0.0;
        d.addPeriodCount(C_USED, "day", 1);
        return EXTRA;
    }
}
