package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10977: first non-forced reroll of the day → pityAfter +{@link #BONUS}.
 * Zero reroll.pity table. Not RerollCoin/LockShard densify (pity nudge).
 */
public final class EmberRerollPityNudge {

    public static final String C_USED = "p1_reroll_pity_nudge";
    public static final int BONUS = 1;

    private EmberRerollPityNudge() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeExtra(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return BONUS;
    }
}
