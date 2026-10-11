package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9057: first life-offer daily-cap forgive of the day (buy once when daily full).
 * Zero offer.daily tables. Not FestivalEntry densify (life, not festival).
 */
public final class EmberLifeDailyCapSoft {

    public static final String C_USED = "p1_life_daily_cap_soft";

    private EmberLifeDailyCapSoft() {}

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
