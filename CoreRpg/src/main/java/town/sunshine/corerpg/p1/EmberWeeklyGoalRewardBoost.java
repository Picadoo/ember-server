package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10017: first weekly-goal completion pay of the week → +{@link #BONUS} badge.
 * Zero goalReward table. Not FestBadge densify (weekly goal, not fest convert).
 */
public final class EmberWeeklyGoalRewardBoost {

    public static final String C_USED = "p1_weekly_goal_reward_boost";
    public static final int BONUS = 1;

    private EmberWeeklyGoalRewardBoost() {}

    public static boolean usedThisWeek(PlayerData d) {
        return d != null && d.periodCount(C_USED, "week") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedThisWeek(d);
    }

    public static int applyPay(PlayerData d, int pay) {
        if (pay < 0 || !ready(d)) return pay;
        d.addPeriodCount(C_USED, "week", 1);
        return pay + BONUS;
    }
}
