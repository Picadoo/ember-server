package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * D611: account-level drop-quality pity for mainline normal/challenge {@code base_item}.
 * Soft floor at 精良 after {@link #NEED} consecutive 标准 drops. Never forces 极品.
 * Does not change {@link EmberRunRules#QUALITY_WEIGHTS} tables.
 */
public final class EmberDropQualityPity {

    public static final String C_PITY = "p1_drop_q_pity";
    public static final int NEED = 5;
    /** 精良 — soft floor when pity is ready. */
    public static final int FLOOR = 1;

    private EmberDropQualityPity() {}

    public static int get(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_PITY, "all"));
    }

    public static String hope(PlayerData d) {
        return EmberHopeMeter.dropQuality(get(d), NEED);
    }

    /** D1323: non-hope remain — 标准连 drop toward 精良 soft floor. */
    public static int clearsLeft(int cur) {
        int c = Math.max(0, cur);
        if (c >= NEED) return 0;
        return NEED - c;
    }

    public static String remainLine(int cur) {
        int c = Math.max(0, cur);
        if (c >= NEED) return "成色软保底已满 · 下次主线装必≥精良";
        int left = clearsLeft(c);
        return "成色软保底 " + c + "/" + NEED + " · 再连出标准" + left + "次触发精良底";
    }

    public static String remainLine(PlayerData d) {
        return remainLine(get(d));
    }

    /**
     * Apply to mainline settle grants: only {@code base_item}. Mutates list entries when floor triggers.
     * @return true when this clear consumed a ready pity (forced floor)
     */
    public static boolean applyToGrants(PlayerData pd, List<EmberRunRules.Grant> grants) {
        if (pd == null || grants == null) return false;
        boolean forced = false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || g.kind != EmberRunRules.Kind.ITEM || g.item == null) continue;
            if (!"base_item".equals(g.key)) continue;
            EmberRunRules.ItemRoll r = g.item;
            int q = r.quality;
            int cur = get(pd);
            if (cur >= NEED && q < FLOOR) {
                q = FLOOR;
                grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount,
                        new EmberRunRules.ItemRoll(r.family, r.slot, r.tier, q, r.craft)));
                forced = true;
            }
            if (q >= FLOOR) {
                if (cur > 0) pd.addPeriodCount(C_PITY, "all", -cur);
            } else {
                pd.addPeriodCount(C_PITY, "all", 1);
            }
            break; // one base_item per clear
        }
        return forced;
    }

    /** True when settleFor should run this pity (mainline ordinary clear). */
    public static boolean applicable(EmberRunSession s, EmberRunMaps.MapDef m) {
        if (s == null || m == null) return false;
        if (s.abyss > 0) return false;
        // D6209: challenge shares soft floor (weights unchanged; NEED unchanged)
        if (m.raid || m.event || m.rush) return false;
        // D6049: short expedition shares soft floor (zero NEED change)
        if (m.shortExpedition) return true;
        String k = m.key == null ? "" : m.key.toLowerCase(Locale.ROOT);
        return k.startsWith("q0") && k.length() == 3;
    }
}
