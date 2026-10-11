package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9697: first brand craft of the day → shard need ×{@link #MULT}.
 * Zero C20 craftShards table. Not BrandSure densify (cost soft, not ConfirmTokens).
 */
public final class EmberBrandCraftSoft {

    public static final String C_USED = "p1_brand_craft_soft";
    public static final double MULT = 0.5;

    private EmberBrandCraftSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int need, boolean consume) {
        if (need <= 0 || !ready(d)) return need;
        int soft = Math.max(1, (int) Math.floor(need * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
