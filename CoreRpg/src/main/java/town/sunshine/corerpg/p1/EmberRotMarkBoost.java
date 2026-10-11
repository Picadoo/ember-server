package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10161: first rotation-mark settle of the day → marks +{@link #BONUS}.
 * Zero rotationBonusMarks table. Not BountyWeight densify (rotation mark).
 */
public final class EmberRotMarkBoost {

    public static final String C_USED = "p1_rot_mark_boost";
    public static final int BONUS = 1;

    private EmberRotMarkBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int marks) {
        if (marks <= 0 || !ready(d)) return marks;
        d.addPeriodCount(C_USED, "day", 1);
        return marks + BONUS;
    }
}
