package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10369: first extra_elite_* settle of the day → amounts ×{@link #MULT}.
 * Zero eliteShard/eliteCore table. Not BaseCore densify (extra elite path).
 */
public final class EmberExtraEliteBoost {

    public static final String C_USED = "p1_extra_elite_boost";
    public static final double MULT = 2.0;

    private EmberExtraEliteBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean apply(PlayerData pd, List<EmberRunRules.Grant> grants) {
        if (pd == null || grants == null || !ready(pd)) return false;
        boolean any = false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || g.key == null || !g.key.startsWith("extra_elite_") || g.amount <= 0) continue;
            if (!any) { pd.addPeriodCount(C_USED, "day", 1); any = true; }
            int amt = Math.max(1, (int) Math.floor(g.amount * MULT));
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, amt, g.item));
        }
        return any;
    }
}
