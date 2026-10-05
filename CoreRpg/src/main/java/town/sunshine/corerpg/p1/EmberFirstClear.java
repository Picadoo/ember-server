package town.sunshine.corerpg.p1;

import java.util.Map;

import town.sunshine.corerpg.PlayerData;

/**
 * D205 (ARCH S1 · REG §4-3): first-clear state split into two keys so a {@code content_version} bump can never roll
 * progress back or erase history.
 * <ul>
 *   <li>fact — {@code p1_first_clear_<map>@all}: the character has cleared the map (or an admin stub set it). Never
 *       deleted by play. Unlocks, gates, hints and the codex read this.</li>
 *   <li>paid — {@code p1_fcpay_<map>@<content version>}: the first-clear package of that content version was paid.
 *       Only the settlement and "first clears stay canonical" checks read this.</li>
 * </ul>
 * Legacy compatibility (pre-D205 writes): {@code p1_first_clear_<map>@<ver>} with {@code ver != all} counts as both
 * fact and paid for that version. Writers call {@link #migrate} first, because {@code addPeriodCount} on
 * {@code @all} drops other periods of the same name. Pure logic, no Bukkit.
 */
public final class EmberFirstClear {
    public static final String C_FACT = EmberForgeService.FLAG_PREFIX; // p1_first_clear_<map>@all
    public static final String C_PAID = "p1_fcpay_";                   // p1_fcpay_<map>@<content version>
    static final String ALL = "all";

    private EmberFirstClear() {}

    /** Has the character first-cleared {@code key} at any content version (or been admin-flagged)? */
    public static boolean fact(PlayerData d, String key) {
        if (d == null || key == null) return false;
        if (d.periodCount(C_FACT + key, ALL) > 0) return true;
        return legacyVersion(d, key) != null;
    }

    /** Was the first-clear package of {@code key} at content version {@code ver} already paid? */
    public static boolean paid(PlayerData d, String key, String ver) {
        if (d == null || key == null || ver == null) return false;
        if (d.periodCount(C_PAID + key, ver) > 0) return true;
        return !ALL.equals(ver) && d.periodCount(C_FACT + key, ver) > 0; // legacy single key
    }

    /** Settlement: the package of {@code ver} is paid and the fact is set. Idempotent. */
    public static void record(PlayerData d, String key, String ver) {
        if (d == null || key == null || ver == null) return;
        migrate(d, key);
        if (d.periodCount(C_PAID + key, ver) <= 0) d.addPeriodCount(C_PAID + key, ver, 1);
        if (d.periodCount(C_FACT + key, ALL) <= 0) d.addPeriodCount(C_FACT + key, ALL, 1);
    }

    /** Admin stub: set or clear only the fact (gates); the paid record is untouched. */
    public static void setFact(PlayerData d, String key, boolean on) {
        if (d == null || key == null) return;
        migrate(d, key);
        int cur = d.periodCount(C_FACT + key, ALL);
        if (on && cur <= 0) d.addPeriodCount(C_FACT + key, ALL, 1);
        if (!on && cur > 0) d.addPeriodCount(C_FACT + key, ALL, -cur);
    }

    /** Admin test hook: set or clear both fact and the paid record of {@code ver} (a clean "never cleared"). */
    public static void setBoth(PlayerData d, String key, String ver, boolean on) {
        if (d == null || key == null || ver == null) return;
        migrate(d, key);
        int paid = d.periodCount(C_PAID + key, ver);
        if (on && paid <= 0) d.addPeriodCount(C_PAID + key, ver, 1);
        if (!on && paid > 0) d.addPeriodCount(C_PAID + key, ver, -paid);
        setFact(d, key, on);
    }

    /** Rewrites a legacy {@code p1_first_clear_<map>@<ver>} into paid@ver + fact@all. Returns true if it changed. */
    public static boolean migrate(PlayerData d, String key) {
        String ver = legacyVersion(d, key);
        if (ver == null) return false;
        if (d.periodCount(C_PAID + key, ver) <= 0) d.addPeriodCount(C_PAID + key, ver, 1);
        d.addPeriodCount(C_FACT + key, ALL, 1); // drops the legacy @ver key (same name, other period)
        return true;
    }

    /** The content version of a legacy single key (value &gt; 0, period != all), or null. */
    static String legacyVersion(PlayerData d, String key) {
        if (d == null || key == null) return null;
        String pre = C_FACT + key + "@";
        for (Map.Entry<String, Integer> e : d.getCounters().entrySet()) {
            String k = e.getKey();
            if (!k.startsWith(pre) || e.getValue() == null || e.getValue().intValue() <= 0) continue;
            String per = k.substring(pre.length());
            if (!per.isEmpty() && !ALL.equals(per)) return per;
        }
        return null;
    }
}
