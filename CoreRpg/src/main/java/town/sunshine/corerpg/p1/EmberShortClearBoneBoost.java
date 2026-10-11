package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10561: first short-expedition {@code *_clear_bone} settle of the day → amount +{@link #BONUS}.
 * Zero short clear table. Not BaseBone densify (short clear bone).
 */
public final class EmberShortClearBoneBoost {

    public static final String C_USED = "p1_short_clear_bone_boost";
    public static final int BONUS = 1;

    private EmberShortClearBoneBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean apply(PlayerData pd, List<EmberRunRules.Grant> grants) {
        if (pd == null || grants == null || !ready(pd)) return false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || g.key == null || !g.key.endsWith("clear_bone") || g.amount <= 0) continue;
            pd.addPeriodCount(C_USED, "day", 1);
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount + BONUS, g.item));
            return true;
        }
        return false;
    }
}
