package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9121: first life-offer weekly-cap forgive of the week (buy once when weekly full).
 * Zero offer.weekly tables. Not LifeDailyCap densify (week vs day).
 */
public final class EmberLifeWeeklyCapSoft {

    public static final String C_USED = "p1_life_weekly_cap_soft";

    private EmberLifeWeeklyCapSoft() {}

    public static boolean usedThisWeek(PlayerData d) {
        return d != null && d.periodCount(C_USED, "week") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedThisWeek(d);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "week", 1);
        return true;
    }
}
