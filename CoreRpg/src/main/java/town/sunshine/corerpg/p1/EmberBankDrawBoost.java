package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8577: first bank→current draw of the day → +{@link #BONUS} stamina (gifted).
 * Zero bank tables. Not TicketConvert densify.
 */
public final class EmberBankDrawBoost {

    public static final String C_USED = "p1_bank_draw_boost";
    public static final int BONUS = 3;

    private EmberBankDrawBoost() {}

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
