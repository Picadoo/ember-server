package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-DA D13332 · 挑战本结算「下一环」菜单化冲钉（challenge_settle_* + 入口常量）。
 * <p>settle 后开薄页 {@code /ember_p1_challenge_settle}；period 记住本局图（q01–q07）；
 * PAPI 供 TrMenu 再挑战/选图/路径。
 * <b>零改</b>挑战掉落 / 体力价 / 成色 / 日帽 / 短征·团本 / variety / soft-reward / 深渊。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberChallengeSettle {

    /** OpenCommand · 薄页 id 同名 */
    public static final String SETTLE_NEXT_LOOP_CMD = "/ember_p1_challenge_settle";
    public static final String SETTLE_NEXT_LOOP_LABEL = "[下一环]";
    public static final String SETTLE_ADVENTURE_CMD = "/ember_p1_adventure";
    public static final String SETTLE_FORGE_CMD = "/ember_p1_forge";
    public static final String SETTLE_CHALLENGE_CMD = "/ember_p1_challenge";

    /** period counter: 1..7 → q01..q07；0 / absent → none */
    public static final String C_LAST = "p1_challenge_settle_last";

    /** Mainline maps that support challenge re-enter. */
    public static final String[] MAINLINE_KEYS = {
            "q01", "q02", "q03", "q04", "q05", "q06", "q07"
    };

    private EmberChallengeSettle() {}

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

    /** Remember last settled challenge map (overwrite; idempotent same key → false). */
    public static boolean remember(PlayerData d, String mapKey) {
        if (d == null) return false;
        int idx = indexOf(mapKey);
        if (idx <= 0) return false;
        int cur = d.periodCount(C_LAST, "all");
        if (cur == idx) return false;
        d.addPeriodCount(C_LAST, "all", idx - cur);
        return true;
    }

    /** Last settled challenge map key, or empty. */
    public static String lastMap(PlayerData d) {
        if (d == null) return "";
        return keyOf(d.periodCount(C_LAST, "all"));
    }

    public static String displayName(EmberRunMaps.MapDef m, String mapKey) {
        if (m != null && m.name != null && !m.name.isEmpty()) return m.name;
        return mapKey == null ? "" : mapKey;
    }

    /** Lore line: map display + path label；无图→空。 */
    public static String line(EmberRunMaps.MapDef m, String mapKey, int pathId) {
        String name = displayName(m, mapKey);
        if (name.isEmpty()) return "";
        String path = EmberChallengePath.label(pathId);
        if (path == null || path.isEmpty() || "未选".equals(path))
            return name;
        return name + " · " + path;
    }
}
