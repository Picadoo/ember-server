package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10177: first pledge-sigmark settle of the day → amount +{@link #BONUS}.
 * Zero S09 table. Not PledgePot densify (settle mark, not pot).
 */
public final class EmberPledgeSettleBoost {

    public static final String C_USED = "p1_pledge_settle_boost";
    public static final int BONUS = 1;

    private EmberPledgeSettleBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int amount) {
        if (amount <= 0 || !ready(d)) return amount;
        d.addPeriodCount(C_USED, "day", 1);
        return amount + BONUS;
    }
}
