package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * D6817: account-level drop-craft pity for mainline {@code base_item}.
 * Soft floor at craft≥1 after {@link #NEED} consecutive craft0 drops.
 * Never forces max craft. Zero {@link EmberRunRules#CRAFT_WEIGHTS} raise.
 * Not DropQualityPity densify (orthogonal craft axis).
 */
public final class EmberDropCraftPity {

    public static final String C_PITY = "p1_drop_c_pity";
    public static final int NEED = 5;
    public static final int FLOOR = 1;

    private EmberDropCraftPity() {}

    public static int get(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_PITY, "all"));
    }

    public static boolean applyToGrants(PlayerData pd, List<EmberRunRules.Grant> grants) {
        if (pd == null || grants == null) return false;
        boolean forced = false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || g.kind != EmberRunRules.Kind.ITEM || g.item == null) continue;
            if (!"base_item".equals(g.key)) continue;
            EmberRunRules.ItemRoll r = g.item;
            int c = r.craft;
            int cur = get(pd);
            if (cur >= NEED && c < FLOOR) {
                c = FLOOR;
                grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount,
                        new EmberRunRules.ItemRoll(r.family, r.slot, r.tier, r.quality, c)));
                forced = true;
            }
            if (c >= FLOOR) {
                if (cur > 0) pd.addPeriodCount(C_PITY, "all", -cur);
            } else {
                pd.addPeriodCount(C_PITY, "all", 1);
                try {
                    int extra = EmberDropCraftNudge.consumeExtra(pd); // D10737
                    if (extra > 0) pd.addPeriodCount(C_PITY, "all", extra);
                } catch (Throwable ignored) { }
            }
            break;
        }
        return forced;
    }

    public static boolean applicable(EmberRunSession s, EmberRunMaps.MapDef m) {
        return EmberDropQualityPity.applicable(s, m);
    }

    public static String remainLine(int cur) {
        int c = Math.max(0, cur);
        if (c >= NEED) return "精工软保底已满 · 下次主线装必≥精工1";
        int left = NEED - c;
        return "精工软保底 " + c + "/" + NEED + " · 再连出精工0×" + left + "触发精工1底";
    }

    public static String remainLine(PlayerData d) {
        return remainLine(get(d));
    }
}
