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

    private EmberGemHoleUnlockSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean unlocked(EmberItemData item, int index, PlayerData d) {
        if (item == null) return false;
        if (item.sockUnlocked(index)) return true;
        if (d == null || (!ready(d) && !usedToday(d))) return false;
        boolean softOk = false;
        if (item.isBlade() && index == 1) softOk = item.enhance >= (5 - DELTA);
        else if (item.isCharm() && index == 0) softOk = item.enhance >= (3 - DELTA);
        if (!softOk) return false;
        if (ready(d)) d.addPeriodCount(C_USED, "day", 1);
        return true;
    }
}
