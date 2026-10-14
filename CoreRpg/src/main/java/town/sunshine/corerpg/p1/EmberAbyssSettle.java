package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-DB D13333 · 深渊结算「下一环」菜单化冲钉（abyss_settle_* + 入口常量）。
 * <p>newBest 后开薄页 {@code /ember_p1_abyss_settle}；period 记住本局 settle 层；
 * PAPI 供 TrMenu 再深渊/选层/层目标/路径。
 * <b>零改</b>深渊掉落 / 体力价 / 层表 / 日帽 / 短征·团本·挑战 / variety / soft-reward。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberAbyssSettle {

    /** OpenCommand · 薄页 id 同名 */
    public static final String SETTLE_NEXT_LOOP_CMD = "/ember_p1_abyss_settle";
    public static final String SETTLE_NEXT_LOOP_LABEL = "[下一环]";
    public static final String SETTLE_ADVENTURE_CMD = "/ember_p1_adventure";
    public static final String SETTLE_FORGE_CMD = "/ember_p1_forge";
    public static final String SETTLE_ABYSS_CMD = "/ember_p1_abyss";

    /** period counter: settle 层号；0 / absent → none */
    public static final String C_LAST = "p1_abyss_settle_last";

    public static final int MAX_FLOOR = EmberAbyssTarget.MAX;

    private EmberAbyssSettle() {}

    /** Remember last settled abyss floor (overwrite; idempotent same floor → false). */
    public static boolean remember(PlayerData d, int floor) {
        if (d == null) return false;
        if (floor < 1 || floor > MAX_FLOOR) return false;
        int cur = d.periodCount(C_LAST, "all");
        if (cur == floor) return false;
        d.addPeriodCount(C_LAST, "all", floor - cur);
        return true;
    }

    /** Last settled floor, or 0. */
    public static int lastFloor(PlayerData d) {
        if (d == null) return 0;
        int v = d.periodCount(C_LAST, "all");
        return v < 1 || v > MAX_FLOOR ? 0 : v;
    }

    /** Display name：第 N 层；无层→空。 */
    public static String displayName(int floor) {
        if (floor < 1) return "";
        return "第 " + floor + " 层";
    }

    /** Lore line: floor name + path label；无层→空。 */
    public static String line(int floor, int pathId) {
        String name = displayName(floor);
        if (name.isEmpty()) return "";
        String path = EmberAbyssPath.label(pathId);
        if (path == null || path.isEmpty() || "未选".equals(path))
            return name;
        return name + " · " + path;
    }
}
