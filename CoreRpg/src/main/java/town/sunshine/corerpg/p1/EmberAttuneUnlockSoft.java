package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9825: first attune unlock of the day → insignia need ×{@link #MULT}.
 * Zero {@link EmberSignature#ALT_MARKS} / C13 table raise.
 */
public final class EmberAttuneUnlockSoft {

    public static final String C_USED = "p1_attune_unlock_soft";
    public static final double MULT = 0.5;

    private EmberAttuneUnlockSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int need(PlayerData d, boolean consume) {
        int base = EmberSignature.altMarks();
        if (base <= 0 || !ready(d)) return base;
        int soft = Math.max(1, (int) Math.floor(base * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
