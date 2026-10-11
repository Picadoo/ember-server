package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10401: first extra_treasure_coin settle of the day → amount ×{@link #MULT}.
 * Zero treasureCoin table. Not ExtraElite/MainlineCoin densify (treasure extra).
 */
public final class EmberExtraTreasureBoost {

    public static final String C_USED = "p1_extra_treasure_boost";
    public static final double MULT = 2.0;

    private EmberExtraTreasureBoost() {}

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
            if (g == null || !"extra_treasure_coin".equals(g.key) || g.amount <= 0) continue;
            pd.addPeriodCount(C_USED, "day", 1);
            int amt = Math.max(1, (int) Math.floor(g.amount * MULT));
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, amt, g.item));
            return true;
        }
        return false;
    }
}
