package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8849: first cook batch of the day → fee ×{@link #MULT}.
 * TIP-AY D12892: {@link #peekFee} for batch gate / show (never consumes);
 * {@link #consumeFee} only before coin deduct.
 * Zero cook.coin_each table. Not BrewCoin densify (cook, not brew).
 */
public final class EmberCookCoinSoft {

    public static final String C_USED = "p1_cook_coin_soft";
    public static final double MULT = 0.5;

    private EmberCookCoinSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /**
     * TIP-AY: pure peek — never consumes (no second day latch).
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
