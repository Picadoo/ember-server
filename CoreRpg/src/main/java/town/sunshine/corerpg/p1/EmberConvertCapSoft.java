package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9249: first convert weekly-cap forgive of the week (one extra convert when weekly full).
 * Zero {@link EmberConvertRules#WEEKLY_CAP}. Not ConvertSure densify (cap soft, not ConfirmTokens).
 * TIP-AW D12860–D12861: preview peek only; confirm path consume.
 */
public final class EmberConvertCapSoft {

    public static final String C_USED = "p1_convert_cap_soft";

    private EmberConvertCapSoft() {}

    public static boolean usedThisWeek(PlayerData d) {
        return d != null && d.periodCount(C_USED, "week") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedThisWeek(d);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "week", 1);
        return true;
    }

    /**
     * TIP-AW: when weekly used already at/over CAP, how many extra converts CapSoft still grants (0 or 1).
     * Pure peek — never consumes.
     */
    public static int effectiveExtra(PlayerData pd, int used) {
        if (pd == null || used < EmberConvertRules.WEEKLY_CAP) return 0;
        return ready(pd) ? 1 : 0;
    }

    /**
     * TIP-AW D12860–D12861: CapSoft gate for convertHeld.
     * {@code !confirm} → peek only (never consume). {@code confirm} → consume if ready.
     * Returns true iff soft opens the weekly-cap gate (caller recomputes refusal with used-1).
     */
    public static boolean tryWeekCapSoft(PlayerData d, boolean confirm) {
        if (d == null) return false;
        if (!confirm) return ready(d);
        return consume(d);
    }
}
