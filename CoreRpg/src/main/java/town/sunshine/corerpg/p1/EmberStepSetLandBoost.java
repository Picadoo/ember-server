package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7457: first 承护/爆闪 step land of the day — heal ×{@link #HEAL_MULT} or slow +{@link #SLOW_EXTRA} ticks.
 * Zero skill_mult. Not HuohenRadius densify (that is ignite search radius).
 */
public final class EmberStepSetLandBoost {

    public static final String C_USED = "p1_step_set_land_boost";
    public static final double HEAL_MULT = 1.5;
    public static final int SLOW_EXTRA = 4;

    private EmberStepSetLandBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
