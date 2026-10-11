package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8097: first forge-roll quality of the day → floor {@link #FLOOR} if rolled lower.
 * Zero QUALITY_W table. Not DropQualityPity densify (forge roll, not drop).
 */
public final class EmberForgeQualityFloor {

    public static final String C_USED = "p1_forge_q_floor";
    public static final int FLOOR = 1;

    private EmberForgeQualityFloor() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeFloor(PlayerData d, int rolled) {
        if (!ready(d) || rolled >= FLOOR) return rolled;
        d.addPeriodCount(C_USED, "day", 1);
        return FLOOR;
    }
}
