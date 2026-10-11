package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10481: first first-clear 核心 grant of the day → amount +{@link #BONUS}.
 * Zero fc table. Not FcBlank/BaseCore densify (fc core package).
 */
public final class EmberFcCoreBoost {

    public static final String C_USED = "p1_fc_core_boost";
    public static final int BONUS = 1;

    private EmberFcCoreBoost() {}

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
            if (g == null || g.key == null || !g.key.startsWith("fc_") || !g.key.endsWith("_core")) continue;
            if (g.amount <= 0) continue;
            pd.addPeriodCount(C_USED, "day", 1);
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount + BONUS, g.item));
            return true;
        }
        return false;
    }
}
