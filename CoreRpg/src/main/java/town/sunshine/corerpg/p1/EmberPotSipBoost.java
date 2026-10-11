package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8353: first heal-potion sip of the day → heal pct ×{@link #MULT}.
 * Zero heal_potion.percent table. Not BossPot densify (amount, not gift).
 */
public final class EmberPotSipBoost {

    public static final String C_USED = "p1_pot_sip_boost";
    public static final double MULT = 1.25;

    private EmberPotSipBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return pct after boost (unchanged if not ready). Consumes when boosted. */
    public static double consumePct(PlayerData d, double pct) {
        if (pct <= 0 || !ready(d)) return pct;
        d.addPeriodCount(C_USED, "day", 1);
        return pct * MULT;
    }
}
