package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9665: first undo-window soft of the day → +{@link #EXTRA_MIN} minutes.
 * Zero gearlib.undo_minutes table. Not forge-cost densify.
 */
public final class EmberUndoWindowSoft {

    public static final String C_USED = "p1_undo_window_soft";
    public static final int EXTRA_MIN = 5;

    private EmberUndoWindowSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Consume on first call of day so the whole day keeps the extended window. */
    public static int apply(PlayerData d, int baseMinutes) {
        if (baseMinutes <= 0 || d == null) return baseMinutes;
        if (ready(d)) d.addPeriodCount(C_USED, "day", 1);
        if (usedToday(d)) return baseMinutes + EXTRA_MIN;
        return baseMinutes;
    }
}
