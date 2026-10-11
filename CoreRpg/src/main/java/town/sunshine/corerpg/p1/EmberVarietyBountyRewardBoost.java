package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10097: first variety-bounty pay of the day → coin/shard ×{@link #MULT}.
 * Zero variety_bounty table. Not VarietyCount densify (reward, not need).
 */
public final class EmberVarietyBountyRewardBoost {

    public static final String C_USED = "p1_variety_bounty_reward_boost";
    public static final double MULT = 2.0;

    private EmberVarietyBountyRewardBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int applyAmount(PlayerData d, int amount, boolean consume) {
        if (amount <= 0 || !ready(d)) return amount;
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(amount * MULT));
    }
}
