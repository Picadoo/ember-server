package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8673: first stamina-potion apply of the day → +{@link #BONUS} stamina.
 * Zero potion_grant / AFK tables. Not Ticket/Bank/StaminaFirst densify.
 */
public final class EmberStaminaPotBoost {

    public static final String C_USED = "p1_stam_pot_boost";
    public static final int BONUS = 5;

    private EmberStaminaPotBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int consumeBonus(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return BONUS;
    }
}
