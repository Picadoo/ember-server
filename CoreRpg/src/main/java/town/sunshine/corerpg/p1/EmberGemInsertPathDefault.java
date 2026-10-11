package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/** D12177: insert omit gem → default GemPath once/day. Not GemCraftPathSync densify. */
public final class EmberGemInsertPathDefault {
    public static final String C_USED = "p1_gem_insert_path_default";
    private EmberGemInsertPathDefault() {}
    public static boolean usedToday(PlayerData d) { return d != null && d.periodCount(C_USED, "day") > 0; }
    public static boolean ready(PlayerData d) { return d != null && !usedToday(d); }
    public static int resolve(PlayerData d) {
        if (d == null || !ready(d)) return 0;
        int code = EmberGemPath.get(d);
        return EmberGemPath.valid(code) ? code : 0;
    }
    public static void consume(PlayerData d) {
        if (d != null && ready(d)) d.addPeriodCount(C_USED, "day", 1);
    }
}
