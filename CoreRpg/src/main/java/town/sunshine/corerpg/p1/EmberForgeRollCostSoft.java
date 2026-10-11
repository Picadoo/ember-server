package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D9889: first forge-roll of the day → marks + mat cost ×{@link #MULT}.
 * Zero C22 tables. Not ForgeQuality/CraftFloor densify (cost soft). Not ExchangeMark densify (forgeroll).
 */
public final class EmberForgeRollCostSoft {

    public static final String C_USED = "p1_forge_roll_cost_soft";
    public static final double MULT = 0.5;

    private EmberForgeRollCostSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int softMarks(PlayerData d, int marks, boolean consume) {
        if (marks <= 0 || !ready(d)) return marks;
        int soft = Math.max(1, (int) Math.floor(marks * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }

    /** Soft mat cost; does not consume (marks soft consumes). */
    public static Cost softCost(Cost c) {
        if (c == null || c.equals(Cost.NONE)) return c;
        return new Cost(h(c.shards), h(c.cores), h(c.blanks), h(c.bone), h(c.coins));
    }

    static int h(int n) {
        if (n <= 0) return 0;
        return Math.max(1, (int) Math.floor(n * MULT));
    }
}
