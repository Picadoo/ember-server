package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9473: first FIRE (block) damage of the day → ×{@link #MULT}.
 * Zero tables. Not LavaSoft densify (FIRE block, not LAVA/FIRE_TICK).
 */
public final class EmberFireBlockSoft {

    public static final String C_USED = "p1_fire_block_soft";
    public static final double MULT = 0.5;

    private EmberFireBlockSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static double consume(PlayerData d, double dmg) {
        if (dmg <= 0 || !ready(d)) return dmg;
        d.addPeriodCount(C_USED, "day", 1);
        return dmg * MULT;
    }
}
