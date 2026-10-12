package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10785: while ready or latched today → unlock thresholds −{@link #DELTA}
 * (blade hole1 ≥4, charm ≥2). Consume on first soft unlock pass.
 * Zero sock table. Not GemCraftCost/Pity densify (hole unlock).
 */
public final class EmberGemHoleUnlockSoft {

    public static final String C_USED = "p1_gem_hole_unlock_soft";
    public static final int DELTA = 1;
    /** TIP-BC list cue · ready only · never consume. */
    public static final String CUE = "今日开孔门槛−1待用（刃二孔≥4·护符≥2）";

    private EmberGemHoleUnlockSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Soft threshold met (no latch). Blade hole1 ≥5−DELTA, charm ≥3−DELTA. */
    public static boolean softEligible(EmberItemData item, int index) {
        if (item == null) return false;
        if (item.isBlade() && index == 1) return item.enhance >= (5 - DELTA);
        if (item.isCharm() && index == 0) return item.enhance >= (3 - DELTA);
        return false;
    }

    public static boolean unlocked(EmberItemData item, int index, PlayerData d) {
        if (item == null) return false;
        if (item.sockUnlocked(index)) return true;
        if (d == null || (!ready(d) && !usedToday(d))) return false;
        if (!softEligible(item, index)) return false;
        if (ready(d)) d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
