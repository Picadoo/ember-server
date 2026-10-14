package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CZ D13331 · 团本结算「下一环」菜单化冲钉（raid_settle_* + 入口常量）。
 * <p>周清后开薄页 {@code /ember_p1_raid_settle}；PAPI 供 TrMenu 周额度/路径本。
 * <b>零改</b> weeklyCap / loot / 体力价 / 团本数值 / Cast / Aid / variety / 短征。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberRaidSettle {

    /** OpenCommand · 薄页 id 同名 */
    public static final String SETTLE_NEXT_LOOP_CMD = "/ember_p1_raid_settle";
    public static final String SETTLE_NEXT_LOOP_LABEL = "[下一环]";
    public static final String SETTLE_ADVENTURE_CMD = "/ember_p1_adventure";
    public static final String SETTLE_FORGE_CMD = "/ember_p1_forge";

    private EmberRaidSettle() {}

    /** 当前路径本 key（r01/r02/r03），未选→空。 */
    public static String settleMap(PlayerData d) {
        String k = EmberRaidPath.mapKey(EmberRaidPath.get(d));
        return k == null ? "" : k;
    }

    public static String displayName(EmberRunMaps.MapDef m, String mapKey, int pathId) {
        if (m != null && m.name != null && !m.name.isEmpty()) return m.name;
        if (EmberRaidPath.valid(pathId)) return EmberRaidPath.label(pathId);
        return mapKey == null ? "" : mapKey;
    }

    /** 本周剩余次数（cap≤0 视为无限→0 灰态不触发）。 */
    public static int weekLeft(int used, int cap) {
        if (cap <= 0) return 0;
        int left = cap - used;
        return left < 0 ? 0 : left;
    }

    /** 周额度文案：{@code used/cap}（cap≤0→空）。 */
    public static String weekLine(int used, int cap) {
        if (cap <= 0) return "";
        return used + "/" + cap;
    }

    /** 再进灰态：额度满。 */
    public static boolean reenterGrey(int used, int cap) {
        return cap > 0 && used >= cap;
    }
}
