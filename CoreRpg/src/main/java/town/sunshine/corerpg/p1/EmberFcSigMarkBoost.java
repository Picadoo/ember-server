package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10225: first fc_sigmark settle of the day → amount +{@link #BONUS}.
 * Zero fcMarks table. Not SigMark densify (first-clear mark).
 */
public final class EmberFcSigMarkBoost {

    public static final String C_USED = "p1_fc_sig_mark_boost";
    public static final int BONUS = 1;

    private EmberFcSigMarkBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int amount) {
        if (amount <= 0 || !ready(d)) return amount;
        d.addPeriodCount(C_USED, "day", 1);
        return amount + BONUS;
    }
}
