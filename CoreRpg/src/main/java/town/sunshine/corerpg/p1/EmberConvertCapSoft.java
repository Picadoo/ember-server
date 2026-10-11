package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9249: first convert weekly-cap forgive of the week (one extra convert when weekly full).
 * Zero {@link EmberConvertRules#WEEKLY_CAP}. Not ConvertSure densify (cap soft, not ConfirmTokens).
 */
public final class EmberConvertCapSoft {

    public static final String C_USED = "p1_convert_cap_soft";

    private EmberConvertCapSoft() {}

    public static boolean usedThisWeek(PlayerData d) {
        return d != null && d.periodCount(C_USED, "week") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedThisWeek(d);
    }

    public static boolean consume(PlayerData d) {
        if (!ready(d)) return false;
        d.addPeriodCount(C_USED, "week", 1);
        return true;
    }
}
