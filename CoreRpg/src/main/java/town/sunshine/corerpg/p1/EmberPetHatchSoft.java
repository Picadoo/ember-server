package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9441: first pet_ashling / pet_cinder life buy of the day → coin ×{@link #MULT}.
 * Zero pet offer tables. Not Offhand/Stable densify (pet hatch).
 */
public final class EmberPetHatchSoft {

    public static final String C_USED = "p1_pet_hatch_soft";
    public static final double MULT = 0.5;

    private EmberPetHatchSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean isPetOffer(String id) {
        return "pet_ashling".equals(id) || "pet_cinder".equals(id);
    }

    public static int consumeCoin(PlayerData d, int coin) {
        if (coin <= 0 || !ready(d)) return coin;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(coin * MULT));
    }
}
