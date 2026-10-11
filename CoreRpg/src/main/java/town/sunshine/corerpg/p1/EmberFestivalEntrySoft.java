package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8737: first festival daily-entry soft-forgive of the day (enter when at cap).
 * Zero daily_entries table. Not Stamina/entry densify.
 */
public final class EmberFestivalEntrySoft {

    public static final String C_USED = "p1_fest_entry_soft";

    private EmberFestivalEntrySoft() {}

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
