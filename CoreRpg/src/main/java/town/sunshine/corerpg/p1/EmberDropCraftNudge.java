package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10737: first drop-craft pity tick of the day → +{@link #BONUS} extra (faster craft floor).
 * Zero NEED table. Not DropCraftPity densify (nudge).
 */
public final class EmberDropCraftNudge {

    public static final String C_USED = "p1_drop_c_nudge";
    public static final int BONUS = 1;

    private EmberDropCraftNudge() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeExtra(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return BONUS;
    }
}
