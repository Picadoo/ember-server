package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10049: first non-raid settle of the day → bounty weight +{@link #BONUS} (base 1 → 2).
 * Zero bounty tier tables. Not RaidCoin/MainlineCoin densify (bounty counter).
 */
public final class EmberBountyWeightBoost {

    public static final String C_USED = "p1_bounty_weight_boost";
    public static final int BONUS = 1;

    private EmberBountyWeightBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return weight after soft (consume when boosted). */
    public static int apply(PlayerData d, int weight, boolean raid) {
        if (raid || weight <= 0 || !ready(d)) return weight;
        d.addPeriodCount(C_USED, "day", 1);
        return weight + BONUS;
    }
}
