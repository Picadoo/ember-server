package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D11873: first insert into an occupied hole of the day → return old gem and seat new (swap).
 * Zero gem flat / craft table. Not GemInsertShard / HoleUnlock densify.
 */
public final class EmberGemSwapSoft {

    public static final String C_USED = "p1_gem_swap_soft";

    private EmberGemSwapSoft() {}

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
