package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9505: first life-offer level-gate forgive of the day (buy once when under lifeLevel).
 * Zero life_level tables. Not LifeDaily/WeeklyCap densify (level gate, not count cap).
 */
public final class EmberLifeLevelGateSoft {

    public static final String C_USED = "p1_life_level_gate_soft";

    private EmberLifeLevelGateSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
