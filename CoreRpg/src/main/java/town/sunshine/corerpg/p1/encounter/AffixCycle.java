package town.sunshine.corerpg.p1.encounter;

/**
 * D241: the shared telegraph clock of the periodic affixes (wall-clock ms, same arithmetic the Director always used):
 * idle until {@code now >= next} (and a player is near) → arm a warning ending at {@code at} → when {@code now >= at}
 * the hit lands → {@code next = now + every}.
 */
public final class AffixCycle {

    private AffixCycle() { }

    /** first cast attempt after promotion: 1.5 s grace + one cadence */
    public static long firstNext(long now, double every) { return now + 1500L + (long) (every * 1000); }

    /** {@code now + secs} in ms (truncating, like every call site before D241) */
    public static long after(long now, double secs) { return now + (long) (secs * 1000); }

    /** cooldown over → may arm a new warning */
    public static boolean ready(long now, long next) { return now >= next; }

    /** an armed warning ({@code at > 0}) */
    public static boolean armed(long at) { return at > 0; }

    /** the armed warning has run out → the hit lands this tick */
    public static boolean lands(long now, long at) { return now >= at; }

    /** number format of the intro lines ("3" not "3.0", "1.5" stays) */
    public static String fmt(double v) { return v == Math.rint(v) ? String.valueOf((int) v) : String.valueOf(v); }
}
