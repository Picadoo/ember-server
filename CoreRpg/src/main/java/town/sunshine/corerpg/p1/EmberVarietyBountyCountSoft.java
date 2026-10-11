package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10081: first variety-bounty progress of the day → need count −{@link #DELTA} (min 1).
 * Zero variety_bounty table. Not BountyWeight densify (variety vb, not daily bounty).
 */
public final class EmberVarietyBountyCountSoft {

    public static final String C_USED = "p1_variety_bounty_count_soft";
    public static final int DELTA = 1;

    private EmberVarietyBountyCountSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int softCount(PlayerData d, int count) {
        if (count <= 1 || d == null) return count;
        if (ready(d) || usedToday(d)) return Math.max(1, count - DELTA);
        return count;
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
