package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9281: first reforge_stone life buy of the day → coin ×{@link #MULT}.
 * Zero reforge_stone offer table. Not Bread/Rod/Brew/SoulDust densify (relic craft).
 */
public final class EmberReforgeStoneSoft {

    public static final String C_USED = "p1_reforge_stone_soft";
    public static final String OFFER_ID = "reforge_stone";
    public static final double MULT = 0.5;

    private EmberReforgeStoneSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeCoin(PlayerData d, int coin) {
        if (coin <= 0 || !ready(d)) return coin;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(coin * MULT));
    }
}
