package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D10657: first gem craft of the day → cost ×{@link #MULT} (min 1 each non-zero).
 * Zero C24 table. Not GemCraftShardRefund densify (pre-pay soft, not post refund).
 */
public final class EmberGemCraftCostSoft {

    public static final String C_USED = "p1_gem_craft_cost_soft";
    public static final double MULT = 0.5;

    private EmberGemCraftCostSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Peek soft cost without consuming. */
    public static Cost peek(PlayerData d, Cost cost) {
        if (cost == null || !ready(d)) return cost;
        return softCost(cost);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "day", 1);
        return true;
    }

    private static Cost softCost(Cost cost) {
        int s = soft(cost.shards), c = soft(cost.cores), b = soft(cost.blanks), o = soft(cost.bone), n = soft(cost.coins);
        if (s == cost.shards && c == cost.cores && b == cost.blanks && o == cost.bone && n == cost.coins) return cost;
        return new Cost(s, c, b, o, n);
    }

    private static int soft(int n) {
        if (n <= 0) return n;
        return Math.max(1, (int) Math.floor(n * MULT));
    }
}
