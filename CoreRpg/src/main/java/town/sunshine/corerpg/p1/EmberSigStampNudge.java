package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10849: first signature-stamp miss of the day → +{@link #BONUS} extra dry streak.
 * Zero STAMP_RATE. Not SigStampPity densify (nudge dry, not force rewrite).
 */
public final class EmberSigStampNudge {

    public static final String C_USED = "p1_sig_stamp_nudge";
    public static final int BONUS = 1;

    private EmberSigStampNudge() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Call after {@link EmberSigStampDry#onMiss}; returns extra dry added. */
    public static int maybeExtra(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        EmberSigStampDry.onMiss(d); // +BONUS via same counter
        return BONUS;
    }
}
