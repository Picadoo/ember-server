package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10497: first first-clear 币 grant of the day → amount +{@link #BONUS}.
 * Zero fc table. Not ExtraTreasure/MainlineCoin densify (fc coin package).
 */
public final class EmberFcCoinBoost {

    public static final String C_USED = "p1_fc_coin_boost";
    public static final int BONUS = 50;

    private EmberFcCoinBoost() {}

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
            if (g == null || g.key == null || !g.key.startsWith("fc_") || !g.key.endsWith("_coin")) continue;
            if (g.amount <= 0) continue;
            pd.addPeriodCount(C_USED, "day", 1);
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount + BONUS, g.item));
            return true;
        }
        return false;
    }
}
