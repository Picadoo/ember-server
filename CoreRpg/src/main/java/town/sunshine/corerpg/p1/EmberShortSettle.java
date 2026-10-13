package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CW D13328 · 短征结算「下一环」薄页 PAPI 冲钉（short_settle_*）。
 * <p>有奖/无奖 settle 后记住本图 key（sx01–sx19）；菜单再进真 {@code enter} 该本。
 * <b>零改</b>日帽、S40–S58、targets.short、Cast/Aid/variety、ShortPath resolve。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberShortSettle {

    /** period counter: 1..19 → sx01..sx19；0 / absent → none */
    public static final String C_LAST = "p1_short_settle_last";

    private EmberShortSettle() {}

    /** 1-based index into {@link EmberShortRules#SHORT_KEYS}, or 0 if unknown. */
    public static int indexOf(String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return 0;
        String k = mapKey.toLowerCase(Locale.ROOT).trim();
        String[] keys = EmberShortRules.SHORT_KEYS;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(k)) return i + 1;
        }
        return 0;
    }

    public static String keyOf(int index) {
        String[] keys = EmberShortRules.SHORT_KEYS;
        if (index < 1 || index > keys.length) return "";
        return keys[index - 1];
    }

    /** Remember last settled short map (idempotent). */
    public static boolean remember(PlayerData d, String mapKey) {
        if (d == null) return false;
        int idx = indexOf(mapKey);
        if (idx <= 0) return false;
        int cur = d.periodCount(C_LAST, "all");
        if (cur == idx) return false;
        d.addPeriodCount(C_LAST, "all", idx - cur);
        return true;
    }

    /** Last settled short map key, or empty. */
    public static String lastMap(PlayerData d) {
        if (d == null) return "";
        return keyOf(d.periodCount(C_LAST, "all"));
    }

    /**
     * Resolve map for settle menu: remembered last → path resolve → todayKey.
     * Never invent sx20 or non-short keys.
     */
    public static String resolveMap(PlayerData d, EmberRunMaps.MapDef pathResolve, String todayKey) {
        String last = lastMap(d);
        if (!last.isEmpty()) return last;
        String rk = EmberShortPathChase.resolveKey(pathResolve);
        if (!rk.isEmpty() && indexOf(rk) > 0) return rk;
        if (todayKey != null && indexOf(todayKey) > 0) return todayKey.toLowerCase(Locale.ROOT).trim();
        return "";
    }

    public static String displayName(EmberRunMaps.MapDef m, String mapKey) {
        if (m != null && m.name != null && !m.name.isEmpty()) return m.name;
        return mapKey == null ? "" : mapKey;
    }
}
