package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8465: first raid settle of the day → +{@link #BONUS} coins (grant).
 * Zero raid reward table. Not MainlineCoin densify (raid, not mainline).
 */
public final class EmberRaidCoinBoost {

    public static final String C_USED = "p1_raid_coin_boost";
    public static final int BONUS = 80;

    private EmberRaidCoinBoost() {}

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
