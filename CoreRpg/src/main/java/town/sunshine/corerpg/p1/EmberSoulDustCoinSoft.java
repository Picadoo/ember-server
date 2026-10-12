package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9089: first soul_dust / soul_dust_bone life buy of the day → coin ×{@link #MULT}.
 * TIP-AZ D12908: {@link #peekCoin} for buy gate / show (never consumes);
 * {@link #consumeCoin} only before spendCoin.
 * Zero soul_dust offer table. Not Bread/Rod/Brew densify (junk craft).
 */
public final class EmberSoulDustCoinSoft {

    public static final String C_USED = "p1_soul_dust_coin_soft";
    public static final double MULT = 0.5;

    private EmberSoulDustCoinSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean isSoulDustOffer(String id) {
        return "soul_dust".equals(id) || "soul_dust_bone".equals(id);
    }

    /**
     * TIP-AZ: pure peek — never consumes (no second day latch).
     * @return coin after soft if ready, else full coin
     */
    public static int peekCoin(PlayerData d, int coin) {
        if (coin <= 0 || !ready(d)) return coin;
        return Math.max(1, (int) Math.floor(coin * MULT));
    }

    /** @return coin after soft (unchanged if not). Consumes when reduced. */
    public static int consumeCoin(PlayerData d, int coin) {
        if (coin <= 0 || !ready(d)) return coin;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(coin * MULT));
    }
}
