package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8289: first fall damage of the day → ×{@link #MULT}.
 * Zero env tables. Not Melee/elite-soft densify.
 */
public final class EmberFallDamageSoft {

    public static final String C_USED = "p1_fall_dmg_soft";
    public static final double MULT = 0.5;

    private EmberFallDamageSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return damage after soft (unchanged if not ready). Consumes when reduced. */
    public static double consume(PlayerData d, double dmg) {
        if (dmg <= 0 || !ready(d)) return dmg;
        d.addPeriodCount(C_USED, "day", 1);
        return dmg * MULT;
    }
}
