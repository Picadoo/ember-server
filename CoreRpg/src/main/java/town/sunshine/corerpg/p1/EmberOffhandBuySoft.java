package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9361: first offhand_ward / offhand_vita life buy of the day → coin ×{@link #MULT}.
 * Zero offhand offer tables. Not Bread/Rod/Brew/SoulDust/Reforge densify (accessory).
 */
public final class EmberOffhandBuySoft {

    public static final String C_USED = "p1_offhand_buy_soft";
    public static final double MULT = 0.5;

    private EmberOffhandBuySoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean isOffhandOffer(String id) {
        return "offhand_ward".equals(id) || "offhand_vita".equals(id);
    }

    public static int consumeCoin(PlayerData d, int coin) {
        if (coin <= 0 || !ready(d)) return coin;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(coin * MULT));
    }
}
