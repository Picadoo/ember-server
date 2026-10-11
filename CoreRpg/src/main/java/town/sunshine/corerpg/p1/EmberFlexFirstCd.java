package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D8017: first flex cast of the day → CD ×{@link #MULT}.
 * Zero flex cooldown_seconds table. Not SigCharge densify (flex, not signature).
 */
public final class EmberFlexFirstCd {

    public static final String C_USED = "p1_flex_first_cd";
    public static final double MULT = 0.85;

    private EmberFlexFirstCd() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** @return cooldown seconds after mult (unchanged if not ready). Consumes when reduced. */
    public static int consumeSecs(PlayerData d, int baseSecs) {
        if (baseSecs <= 0 || !ready(d)) return baseSecs;
        d.addPeriodCount(C_USED, "day", 1);
        return Math.max(1, (int) Math.floor(baseSecs * MULT));
    }
}
