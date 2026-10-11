package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8801: first grilled-fish bite of the day → +{@link #EXTRA} hunger/sat.
 * Zero BITE_RESTORE table. Not PotSip densify.
 */
public final class EmberBiteRestoreBoost {

    public static final String C_USED = "p1_bite_restore_boost";
    public static final int EXTRA = 2;

    private EmberBiteRestoreBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeExtra(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return EXTRA;
    }
}
