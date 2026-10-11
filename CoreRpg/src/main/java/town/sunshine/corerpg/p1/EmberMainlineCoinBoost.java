package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8417: first fresh mainline settle of the day → +{@link #BONUS} coins (grant).
 * Zero mainline coin table. Not ChalCore densify (coin, not core).
 */
public final class EmberMainlineCoinBoost {

    public static final String C_USED = "p1_main_coin_boost";
    public static final int BONUS = 50;

    private EmberMainlineCoinBoost() {}

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
