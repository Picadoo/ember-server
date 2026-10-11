package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10625: first rush paid settle of the day → amount +{@link #BONUS}.
 * Zero rush table. Not FestBadge densify (rush weekly badge).
 */
public final class EmberRushBadgeBoost {

    public static final String C_USED = "p1_rush_badge_boost";
    public static final int BONUS = 1;

    private EmberRushBadgeBoost() {}

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
