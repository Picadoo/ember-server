package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7073: first set-identity 烬斩 land of the day — sustain heal ×{@link #HEAL_MULT} or burst slow +{@link #SLOW_EXTRA} ticks.
 * Zero skill_mult / base heal table. Not IgniteRefresh densify.
 */
public final class EmberSetLandFirstBoost {

    public static final String C_USED = "p1_set_land_first";
    public static final double HEAL_MULT = 1.5;
    public static final int SLOW_EXTRA = 4;

    private EmberSetLandFirstBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return true when boost applies (consumes day latch). */
    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
