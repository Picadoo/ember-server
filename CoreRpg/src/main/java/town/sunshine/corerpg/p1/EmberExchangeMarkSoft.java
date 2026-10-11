package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9857: first mark-exchange of the day → marks need ×{@link #MULT}.
 * Zero {@link EmberRunRules#marksPerExchange()} / C07 table raise.
 */
public final class EmberExchangeMarkSoft {

    public static final String C_USED = "p1_exchange_mark_soft";
    public static final double MULT = 0.5;

    private EmberExchangeMarkSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int need(PlayerData d, boolean consume) {
        int base = EmberRunRules.marksPerExchange();
        if (base <= 0 || !ready(d)) return base;
        int soft = Math.max(1, (int) Math.floor(base * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
