package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10289: first {@code base_bone} settle of the day → amount +{@link #BONUS}.
 * Zero base mat table. Not MainlineCoin/AffixShard densify (base 骨头).
 */
public final class EmberBaseBoneBoost {

    public static final String C_USED = "p1_base_bone_boost";
    public static final int BONUS = 1;

    private EmberBaseBoneBoost() {}

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
            if (g == null || !"base_bone".equals(g.key) || g.amount <= 0) continue;
            pd.addPeriodCount(C_USED, "day", 1);
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount + BONUS, g.item));
            return true;
        }
        return false;
    }
}
