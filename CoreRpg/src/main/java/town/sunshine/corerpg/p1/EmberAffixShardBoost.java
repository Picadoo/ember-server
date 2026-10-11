package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D7585: when variety affixDone settles, +{@link #BONUS} affix shard once per uid@day.
 * Zero affixShard table raise. Not DropCraft/BulkBlank densify.
 */
public final class EmberAffixShardBoost {

    public static final String C_USED = "p1_affix_shard_boost";
    public static final int BONUS = 1;

    private EmberAffixShardBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Append bonus grant when affixDone and ready. @return true when boosted. */
    public static boolean apply(PlayerData pd, boolean affixDone, List<EmberRunRules.Grant> grants) {
        if (pd == null || !affixDone || grants == null || !ready(pd)) return false;
        boolean hasAffix = false;
        for (EmberRunRules.Grant g : grants) {
            if (g != null && "var_affix_shard".equals(g.key)) { hasAffix = true; break; }
        }
        if (!hasAffix) return false;
        pd.addPeriodCount(C_USED, "day", 1);
        grants.add(new EmberRunRules.Grant("var_affix_shard_boost", EmberRunRules.Kind.MAT,
                EmberUpgradeRules.MAT_SHARD, BONUS, null));
        return true;
    }
}
