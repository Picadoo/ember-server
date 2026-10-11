package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8961: first cook batch of the day → life XP ×{@link #MULT}.
 * Zero cookXp table. Not FishXp densify (cook, not fish).
 */
public final class EmberCookXpBoost {

    public static final String C_USED = "p1_cook_xp_boost";
    public static final int MULT = 2;

    private EmberCookXpBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeXp(PlayerData d, int base) {
        if (base <= 0 || !ready(d)) return base;
        d.addPeriodCount(C_USED, "day", 1);
        return base * MULT;
    }
}
