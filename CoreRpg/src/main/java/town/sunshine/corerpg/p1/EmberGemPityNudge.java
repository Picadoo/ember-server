package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10673: first gem-pity tick of the day → +{@link #BONUS} extra pity (less clears to stone).
 * Zero PITY_NEED table. Not GemCraftCost densify (pity path).
 */
public final class EmberGemPityNudge {

    public static final String C_USED = "p1_gem_pity_nudge";
    public static final int BONUS = 1;

    private EmberGemPityNudge() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return extra pity to add (0 or BONUS); consumes when >0. */
    public static int consumeExtra(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return BONUS;
    }
}
