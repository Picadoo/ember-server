package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8129: first forge-roll craft of the day → floor {@link #FLOOR} if rolled lower.
 * Zero CRAFT_W table. Not DropQuality densify.
 */
public final class EmberForgeCraftFloor {

    public static final String C_USED = "p1_forge_craft_floor";
    public static final int FLOOR = 1;

    private EmberForgeCraftFloor() {}

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
