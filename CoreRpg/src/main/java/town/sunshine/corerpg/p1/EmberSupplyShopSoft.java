package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9809: first supply-shop buy of the day → unit price ×{@link #MULT}.
 * TIP-BA D12924: {@link #peekPrice} for preview / afford gate (never consumes);
 * {@link #apply}(..., true) only when n>0 and before spendCoin.
 * Zero C14 table. Not PotSip/Brew densify (shop price, not pot effect).
 */
public final class EmberSupplyShopSoft {

    public static final String C_USED = "p1_supply_shop_soft";
    public static final double MULT = 0.5;

    private EmberSupplyShopSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /**
     * TIP-BA: pure peek — never consumes (no second day latch).
     * ≡ {@link #apply}(d, price, false).
     * @return price after soft if ready, else full price
     */
    public static int peekPrice(PlayerData d, int price) {
        if (price <= 0 || !ready(d)) return price;
        return Math.max(1, (int) Math.floor(price * MULT));
    }

    public static int apply(PlayerData d, int price, boolean consume) {
        if (price <= 0 || !ready(d)) return price;
        int soft = Math.max(1, (int) Math.floor(price * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
