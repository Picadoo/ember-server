package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7617: first signature skill_charge wind-up of the day ×{@link #MULT}.
 * Zero base skill_charge / skill_mult. Not ChargeMiss densify.
 */
public final class EmberSigChargeFirst {

    public static final String C_USED = "p1_sig_charge_first";
    public static final double MULT = 0.85;

    private EmberSigChargeFirst() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return multiplier to apply (1.0 if not ready). Consumes when &lt; 1. */
    public static double consumeMult(PlayerData d) {
        if (!ready(d)) return 1.0;
        d.addPeriodCount(C_USED, "day", 1);
        return MULT;
    }
}
