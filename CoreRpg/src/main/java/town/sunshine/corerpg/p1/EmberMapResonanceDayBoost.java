package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D11857: after first map-resonance enter cue of the day → day latch; while active +
 * resonance, slash geometry gets a thin extra widen (zero skill_mult).
 * Not MapResonance densify table; not tip twin.
 */
public final class EmberMapResonanceDayBoost {

    public static final String C_DAY = "p1_map_reso_day_boost";

    /** Extra range (ring) while day boost + resonance. */
    public static final double EXTRA_RANGE = 0.08;
    /** Extra line length (line) while day boost + resonance. */
    public static final double EXTRA_LINE = 0.10;
    /** Extra line width while day boost + resonance. */
    public static final double EXTRA_LINE_W = 0.03;

    private EmberMapResonanceDayBoost() {}

    public static boolean activeToday(PlayerData d) {
        return d != null && d.periodCount(C_DAY, "day") > 0;
    }

    /** Latch on first resonance enter of the day. */
    public static boolean maybeLatch(PlayerData d) {
        if (d == null || activeToday(d)) return false;
        d.addPeriodCount(C_DAY, "day", 1);
        return true;
    }

    public static boolean boost(PlayerData d, boolean resonance) {
        return resonance && activeToday(d);
    }
}
