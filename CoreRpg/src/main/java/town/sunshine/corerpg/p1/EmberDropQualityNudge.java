package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10721: first drop-quality pity tick of the day → +{@link #BONUS} extra (faster 精良 floor).
 * Zero NEED table. Not DropQualityPity densify (nudge, not floor rewrite).
 */
public final class EmberDropQualityNudge {

    public static final String C_USED = "p1_drop_q_nudge";
    public static final int BONUS = 1;

    private EmberDropQualityNudge() {}

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
