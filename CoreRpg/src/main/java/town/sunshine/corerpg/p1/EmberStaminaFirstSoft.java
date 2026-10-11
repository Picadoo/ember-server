package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8481: first run stamina reserve of the day → cost −{@link #LESS} (min 1).
 * Zero C01 stamina table / AFK. Not coin-settle densify.
 */
public final class EmberStaminaFirstSoft {

    public static final String C_USED = "p1_stamina_first_soft";
    public static final int LESS = 5;

    private EmberStaminaFirstSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return cost after soft. Consumes when reduced. */
    public static int consumeCost(PlayerData d, int cost) {
        if (cost <= 1 || !ready(d)) return cost;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, cost - LESS);
    }
}
