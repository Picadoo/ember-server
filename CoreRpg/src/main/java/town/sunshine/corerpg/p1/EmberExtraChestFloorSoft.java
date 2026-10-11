package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D10417: first extra_chest_item of the day → quality ≥ {@link #FLOOR} (精良).
 * Zero quality weight tables. Not DropQualityPity densify (extra chest, not base_item).
 */
public final class EmberExtraChestFloorSoft {

    public static final String C_USED = "p1_extra_chest_floor_soft";
    public static final int FLOOR = 1; // 精良

    private EmberExtraChestFloorSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static boolean apply(PlayerData pd, List<EmberRunRules.Grant> grants) {
        if (pd == null || grants == null || !ready(pd)) return false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || !"extra_chest_item".equals(g.key) || g.item == null) continue;
            EmberRunRules.ItemRoll r = g.item;
            if (r.quality >= FLOOR) continue; // wait for a sub-floor chest
            pd.addPeriodCount(C_USED, "day", 1);
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount,
                    new EmberRunRules.ItemRoll(r.family, r.slot, r.tier, FLOOR, r.craft)));
            return true;
        }
        return false;
    }
}
