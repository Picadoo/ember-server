package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * TIP-CV D13327 · 短征选页「路径追」可点 — PAPI / enter 冲钉（只读展示 + resolve 复用）。
 * <p>复用 {@link EmberShortPath#resolve} / {@link EmberShortPath#label} / {@link EmberShortPath#tip}；
 * <b>零改</b> resolve 排序/权重、日帽、S40–S58、targets.short、Cast/Aid/variety、日差 short_div*。
 * <p>{@code _ok}：path 有效（非 NONE）∧ resolve 非空 → {@code 1}；未选 path → {@code 0}（禁静默改偏好）。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberShortPathChase {

    private EmberShortPathChase() {}

    /** Stable path token for {@code short_path}: {@code 0}/{@code 1}/{@code 2}/{@code 3}. */
    public static String token(int path) {
        if (path == EmberShortPath.FC) return "1";
        if (path == EmberShortPath.DAY) return "2";
        if (path == EmberShortPath.LATE) return "3";
        return "0";
    }

    /** Resolve map key; empty when none. */
    public static String resolveKey(EmberRunMaps.MapDef m) {
        return m == null || m.key == null || m.key.isEmpty() ? "" : m.key;
    }

    /** Resolve display name; empty when none. */
    public static String resolveName(EmberRunMaps.MapDef m) {
        if (m == null) return "";
        if (m.name != null && !m.name.isEmpty()) return m.name;
        return m.key == null ? "" : m.key;
    }

    /**
     * TrMenu single check: path valid ∧ resolve non-empty → {@code "1"}, else {@code "0"}.
     * Unselected (NONE) always {@code "0"} — never silently rewrite preference.
     */
    public static String ok(int path, String resolveKey) {
        if (!EmberShortPath.valid(path)) return "0";
        return resolveKey != null && !resolveKey.isEmpty() ? "1" : "0";
    }

    /**
     * Menu-friendly one-liner (plain text, no §).
     * NONE → {@code 路径追 · 未选}; empty resolve → {@code <label> · 暂无下一本};
     * else {@code <label> · 下一本 SXNN name} (resolveLine style).
     */
    public static String line(int path, String resolveKey, String resolveName) {
        if (!EmberShortPath.valid(path)) return "路径追 · 未选";
        String lab = EmberShortPath.label(path);
        if (resolveKey == null || resolveKey.isEmpty()) return lab + " · 暂无下一本";
        String nm = (resolveName == null || resolveName.isEmpty()) ? resolveKey : resolveName;
        return lab + " · 下一本 " + resolveKey.toUpperCase(Locale.ROOT) + " " + nm;
    }
}
