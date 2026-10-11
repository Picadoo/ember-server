package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8929: first STARVATION damage of the day → cancel.
 * Zero tables. Not Suffocation/Drown densify.
 */
public final class EmberStarvationSoft {

    public static final String C_USED = "p1_starve_soft";

    private EmberStarvationSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean consumeCancel(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
