package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9953: first forge-roll of the day → free quality floor (≥精良) without floor surcharge.
 * Zero C22 floor tables / QUALITY_W. Not ForgeRollCostSoft densify (floor, not cost).
 * Not ForgeQualityFloor densify (forgeroll path, not enhance quality op).
 */
public final class EmberForgeRollFloorSoft {

    public static final String C_USED = "p1_forge_roll_floor_soft";

    private EmberForgeRollFloorSoft() {}

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
