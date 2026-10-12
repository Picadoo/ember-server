package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8529: first abyss floor fee of the day → ×{@link #MULT} (coin/mark fee amount).
 * TIP-AX D12876: {@link #peekFee} for entryProblems/abyss_hope (never consumes);
 * {@link #consumeFee} only on SessionService reserve.
 * Zero abyss fee table. Not StaminaFirst densify.
 */
public final class EmberAbyssFeeSoft {

    public static final String C_USED = "p1_abyss_fee_soft";
    public static final double MULT = 0.8;

    private EmberAbyssFeeSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /**
     * TIP-AX: pure peek — never consumes (no second day latch).
     * @return fee after soft if ready, else full fee
     */
    public static int peekFee(PlayerData d, int fee) {
        if (fee <= 0 || !ready(d)) return fee;
        return Math.max(1, (int) Math.floor(fee * MULT));
    }

    /** @return fee after soft (unchanged if not). Consumes when reduced. */
    public static int consumeFee(PlayerData d, int fee) {
        if (fee <= 0 || !ready(d)) return fee;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(fee * MULT));
    }
}

