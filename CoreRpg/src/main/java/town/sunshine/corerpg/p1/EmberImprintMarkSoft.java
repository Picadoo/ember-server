package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9985: first imprint of the day → insignia need ×{@link #MULT}.
 * Zero imprintMarks / STAMP_RATE. Not ImprintCoinSoft densify (marks, not coin).
 */
public final class EmberImprintMarkSoft {

    public static final String C_USED = "p1_imprint_mark_soft";
    public static final double MULT = 0.5;

    private EmberImprintMarkSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int need(PlayerData d, boolean consume) {
        int base = EmberSignature.imprintMarks();
        if (base <= 0 || !ready(d)) return base;
        int soft = Math.max(1, (int) Math.floor(base * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
