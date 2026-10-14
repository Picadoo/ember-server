package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-DD D13335 · 精选周帽满 / FeaturedPath.afterSettle「下一环」菜单化冲钉（featured_settle_* + 入口常量）。
 * <p>精选图结算或周帽满后开薄页 {@code /ember_p1_featured_settle}；period 记住 settle 图/路径/周帽态；
 * PAPI 供 TrMenu 再进/换路径/冒险/工坊/图装追。
 * <b>零改</b>掉落 / 体力价 / 周帽数值 / 精选语义 / Cast·Aid / variety / 已齐 settle 薄页 / 庭内。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberFeaturedSettle {

    /** OpenCommand · 薄页 id 同名 */
    public static final String SETTLE_NEXT_LOOP_CMD = "/ember_p1_featured_settle";
    public static final String SETTLE_NEXT_LOOP_LABEL = "[下一环]";
    public static final String SETTLE_ADVENTURE_CMD = "/ember_p1_adventure";
    public static final String SETTLE_FORGE_CMD = "/ember_p1_forge";

    /** period counter: 1..7 → q01..q07；0 / absent → none */
    public static final String C_LAST = "p1_featured_settle_last";
    /** period counter: 1=challenge 2=normal（同 {@link EmberFeaturedPath}）；0 → none */
    public static final String C_PATH = "p1_featured_settle_path";
    /** period counter: 1=周帽已满；0 → 未满/未知 */
    public static final String C_CAP = "p1_featured_settle_cap";

    /** Mainline maps that can be weekly featured. */
    public static final String[] MAINLINE_KEYS = {
            "q01", "q02", "q03", "q04", "q05", "q06", "q07"
    };

    private EmberFeaturedSettle() {}

    /** 1-based index into {@link #MAINLINE_KEYS}, or 0 if unknown. */
    public static int indexOf(String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return 0;
        String k = mapKey.toLowerCase(Locale.ROOT).trim();
        for (int i = 0; i < MAINLINE_KEYS.length; i++) {
            if (MAINLINE_KEYS[i].equals(k)) return i + 1;
        }
        return 0;
    }

    public static String keyOf(int index) {
        if (index < 1 || index > MAINLINE_KEYS.length) return "";
        return MAINLINE_KEYS[index - 1];
    }

    /**
     * Remember last featured settle map/path/week-cap (overwrite).
     * Returns true if any field changed. Requires valid mainline map.
     * pathId may be {@link EmberFeaturedPath#NONE} (0); capFull → 1/0.
     */
    public static boolean remember(PlayerData d, String mapKey, int pathId, boolean capFull) {
        if (d == null) return false;
        int mIdx = indexOf(mapKey);
        if (mIdx <= 0) return false;
        int pIdx = EmberFeaturedPath.valid(pathId) ? pathId : EmberFeaturedPath.NONE;
        int cIdx = capFull ? 1 : 0;
        boolean changed = false;
        int curM = d.periodCount(C_LAST, "all");
        if (curM != mIdx) {
            d.addPeriodCount(C_LAST, "all", mIdx - curM);
            changed = true;
        }
        int curP = d.periodCount(C_PATH, "all");
        if (curP != pIdx) {
            d.addPeriodCount(C_PATH, "all", pIdx - curP);
            changed = true;
        }
        int curC = d.periodCount(C_CAP, "all");
        if (curC != cIdx) {
            d.addPeriodCount(C_CAP, "all", cIdx - curC);
            changed = true;
        }
        return changed;
    }

    /** Last settled map key, or empty. */
    public static String lastMap(PlayerData d) {
        if (d == null) return "";
        return keyOf(d.periodCount(C_LAST, "all"));
    }

    /** Last settled featured path id, or {@link EmberFeaturedPath#NONE}. */
    public static int lastPath(PlayerData d) {
        if (d == null) return EmberFeaturedPath.NONE;
        int v = d.periodCount(C_PATH, "all");
        return EmberFeaturedPath.valid(v) ? v : EmberFeaturedPath.NONE;
    }

    /** Whether last settle remembered week-cap-full. */
    public static boolean lastCapFull(PlayerData d) {
        if (d == null) return false;
        return d.periodCount(C_CAP, "all") > 0;
    }

    public static String displayName(EmberRunMaps.MapDef m, String mapKey) {
        if (m != null && m.name != null && !m.name.isEmpty()) return m.name;
        return mapKey == null ? "" : mapKey;
    }

    /** Lore line: 图名 · 路径 · 周帽态；无图→空。 */
    public static String line(EmberRunMaps.MapDef m, String mapKey, int pathId, boolean capFull) {
        String name = displayName(m, mapKey);
        if (name.isEmpty()) return "";
        String path = EmberFeaturedPath.label(pathId);
        String cap = capFull ? "周帽已满" : "周帽未满";
        if (path == null || path.isEmpty() || "未选".equals(path))
            return name + " · " + cap;
        return name + " · " + path + " · " + cap;
    }
}
