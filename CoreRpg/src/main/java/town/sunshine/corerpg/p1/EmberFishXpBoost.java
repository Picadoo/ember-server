package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8865: first successful fish catch of the day → life XP ×{@link #MULT}.
 * Zero fish_xp table. Not Bite/Cook densify.
 */
public final class EmberFishXpBoost {

    public static final String C_USED = "p1_fish_xp_boost";
    public static final int MULT = 2;
    /** TIP-BB D12942: show ready tail · never consume. */
    public static final String CUE = "今日首钓加验×2待用";

    private EmberFishXpBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return XP after boost. Consumes when multiplied. */
    public static int consumeXp(PlayerData d, int base) {
        if (base <= 0 || !ready(d)) return base;
        d.addPeriodCount(C_USED, "day", 1);
        return base * MULT;
    }
}
