package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D9617: first refine forge cost of the day → materials/coins ×{@link #MULT}.
 * Zero economy tables. Not ForgeQuality/CraftFloor densify (cost soft, not RNG floor).
 * Not sure-do densify.
 */
public final class EmberRefineCostSoft {

    public static final String C_USED = "p1_refine_cost_soft";
    public static final double MULT = 0.5;

    private EmberRefineCostSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Preview: soft cost if ready, no consume. Pay: consume then soft. */
    public static Cost apply(PlayerData d, Cost c, boolean consume) {
        if (c == null || c.equals(Cost.NONE) || !ready(d)) return c;
        Cost soft = half(c);
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }

    static Cost half(Cost c) {
        return new Cost(h(c.shards), h(c.cores), h(c.blanks), h(c.bone), h(c.coins));
    }

    static int h(int n) {
        if (n <= 0) return 0;
        return Math.max(1, (int) Math.floor(n * MULT));
    }
}
