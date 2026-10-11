package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10145: when variety eventDone settles, +{@link #BONUS} core once per day.
 * Zero eventCore table. Not AffixShard densify (event core, not affix shard).
 */
public final class EmberEventCoreBoost {

    public static final String C_USED = "p1_event_core_boost";
    public static final int BONUS = 1;

    private EmberEventCoreBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean apply(PlayerData pd, boolean eventDone, List<EmberRunRules.Grant> grants) {
        if (pd == null || !eventDone || grants == null || !ready(pd)) return false;
        boolean has = false;
        for (EmberRunRules.Grant g : grants) {
            if (g != null && g.key != null && g.key.startsWith("var_event_")) { has = true; break; }
        }
        if (!has) return false;
        pd.addPeriodCount(C_USED, "day", 1);
        grants.add(new EmberRunRules.Grant("var_event_core_boost", EmberRunRules.Kind.MAT,
                EmberUpgradeRules.MAT_CORE, BONUS, null));
        return true;
    }
}
