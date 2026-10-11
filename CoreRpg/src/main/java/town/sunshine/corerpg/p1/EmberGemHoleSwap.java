package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D11969: first sock0↔sock1 swap of the day (both holes unlocked, at least one filled).
 * Zero gem flat / craft table. Not GemSwapSoft densify (hole↔hole, not bag replace).
 */
public final class EmberGemHoleSwap {

    public static final String C_USED = "p1_gem_hole_swap";

    private EmberGemHoleSwap() {}

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

    /** True when piece can swap holes content. */
    public static boolean canSwap(EmberItemData d) {
        if (d == null || d.sockMax() < 2) return false;
        if (!d.sockUnlocked(0) || !d.sockUnlocked(1)) return false;
        return d.sockAt(0) != 0 || d.sockAt(1) != 0;
    }
}
