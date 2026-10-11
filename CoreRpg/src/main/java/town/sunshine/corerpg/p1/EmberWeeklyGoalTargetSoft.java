package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10033: first weekly-goal target soft of the week → target −{@link #DELTA} (min 1).
 * Zero goalTargets table. Not WeeklyGoalReward densify (target, not pay).
 */
public final class EmberWeeklyGoalTargetSoft {

    public static final String C_USED = "p1_weekly_goal_target_soft";
    public static final int DELTA = 1;

    private EmberWeeklyGoalTargetSoft() {}

    public static boolean usedThisWeek(PlayerData d) {
        return d != null && d.periodCount(C_USED, "week") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedThisWeek(d);
    }

    /** Soft target while ready or already latched this week. */
    public static int softTarget(PlayerData d, int target) {
        if (target <= 1 || d == null) return target;
        if (ready(d) || usedThisWeek(d)) return Math.max(1, target - DELTA);
        return target;
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "week", 1);
        return true;
    }
}
