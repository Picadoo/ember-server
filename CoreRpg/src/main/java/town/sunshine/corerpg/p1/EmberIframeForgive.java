package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8401: first melee that would be fully eaten by i-frame → deal {@link #FRAC} of full once/day.
 * Zero formula tables. Not Fall/MeleeSlow densify.
 */
public final class EmberIframeForgive {

    public static final String C_USED = "p1_iframe_forgive";
    public static final double FRAC = 0.35;

    private EmberIframeForgive() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return damage to apply (&gt;0) when forgiving; 0 if not. Consumes when &gt;0. */
    public static double consume(PlayerData d, double full) {
        if (full <= 0 || !ready(d)) return 0.0;
        d.addPeriodCount(C_USED, "day", 1);
        return full * FRAC;
    }
}
