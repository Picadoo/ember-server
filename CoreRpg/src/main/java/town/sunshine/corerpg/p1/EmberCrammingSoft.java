package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9217: first CRAMMING damage of the day → ×{@link #MULT}.
 * Zero tables. Outside prior env soft densify families.
 */
public final class EmberCrammingSoft {

    public static final String C_USED = "p1_cramming_soft";
    public static final double MULT = 0.5;

    private EmberCrammingSoft() {}

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
