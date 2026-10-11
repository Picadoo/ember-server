package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9425: first stable_charm life buy of the day → coin ×{@link #MULT}.
 * Zero stable_charm offer table. Not prior life-coin densify (pearl→charm).
 */
public final class EmberStableCharmSoft {

    public static final String C_USED = "p1_stable_charm_soft";
    public static final String OFFER_ID = "stable_charm";
    public static final double MULT = 0.5;

    private EmberStableCharmSoft() {}

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
