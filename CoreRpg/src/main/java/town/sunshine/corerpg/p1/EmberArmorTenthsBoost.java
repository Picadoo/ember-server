package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D7169: armor dismantle tenths ledger +{@link #BONUS} once per uid@day.
 * Zero armorDismantleTenths / six-slot table raise.
 */
public final class EmberArmorTenthsBoost {

    public static final String C_USED = "p1_armor_tenths_boost";
    public static final int BONUS = 1;

    private EmberArmorTenthsBoost() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return bonus tenths to add (0 if not ready). Consumes day latch. */
    public static int consume(PlayerData d) {
        if (!ready(d)) return 0;
        d.addPeriodCount(C_USED, "day", 1);
        return BONUS;
    }
}
