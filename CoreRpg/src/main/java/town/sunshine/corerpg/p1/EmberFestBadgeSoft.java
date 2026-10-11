package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D10001: first fest badge convert of the day → badgeRate ×{@link #MULT} (cheaper).
 * Zero badgeRate table. Not FestivalEntry densify (convert rate, not entry).
 */
public final class EmberFestBadgeSoft {

    public static final String C_USED = "p1_fest_badge_soft";
    public static final double MULT = 0.5;

    private EmberFestBadgeSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    /** Soft coins-per-badge (lower = cheaper). */
    public static int rate(PlayerData d, int badgeRate, boolean consume) {
        if (badgeRate <= 1 || !ready(d)) return badgeRate;
        int soft = Math.max(1, (int) Math.floor(badgeRate * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
