package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8545: first successful ticket→stamina convert of the day → +{@link #BONUS} stamina.
 * Zero ticketConvert table / AFK. Not StaminaFirst densify (convert, not entry).
 */
public final class EmberTicketConvertBoost {

    public static final String C_USED = "p1_ticket_convert_boost";
    public static final int BONUS = 5;

    private EmberTicketConvertBoost() {}

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
