package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8641: first drowning damage of the day → cancel (×0).
 * Zero tables. Not Fall/Explosion/Poison densify (drown).
 */
public final class EmberDrownSoft {

    public static final String C_USED = "p1_drown_soft";

    private EmberDrownSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return true if cancelled (consumed). */
    public static boolean consumeCancel(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
