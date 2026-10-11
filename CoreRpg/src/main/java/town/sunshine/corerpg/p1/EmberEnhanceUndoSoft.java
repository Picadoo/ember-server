package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9873: first enhance-fail undo window of the day → +{@link #EXTRA_MS}.
 * Zero RATE / MAX_TRIES. Not UndoWindowSoft densify (enhance-fail, not dismantle).
 */
public final class EmberEnhanceUndoSoft {

    public static final String C_USED = "p1_enhance_undo_soft";
    public static final long EXTRA_MS = 30_000L;

    private EmberEnhanceUndoSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Consume on first fail-snap of day; return effective window ms. */
    public static long windowMs(PlayerData d) {
        long base = EmberEnhanceUndo.WINDOW_MS;
        if (d == null) return base;
        if (ready(d)) d.addPeriodCount(C_USED, "day", 1);
        if (usedToday(d)) return base + EXTRA_MS;
        return base;
    }
}
