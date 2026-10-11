package town.sunshine.corerpg.p1;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * D6625: when map marks are imprintMarks−1 before settle grant, double this clear's sig_mark once.
 * Zero imprintMarks / STAMP_RATE raise. Less imprint-gate despair.
 */
public final class EmberMarkNearBreak {

    public static final String C_NEAR = "p1_mark_near_";

    private EmberMarkNearBreak() {}

    public static boolean maybeBoost(PlayerData d, String mapKey, List<EmberRunRules.Grant> grants) {
        if (d == null || mapKey == null || grants == null) return false;
        int need = EmberSignature.imprintMarks();
        if (need <= 1) return false;
        int cur = d.periodCount(EmberSignature.C_MARK + mapKey, "all");
        if (cur != need - 1) return false;
        String latch = C_NEAR + mapKey;
        if (d.periodCount(latch, "all") > 0) return false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || g.kind != EmberRunRules.Kind.SIGMARK) continue;
            if (!mapKey.equals(g.id)) continue;
            if (!"sig_mark".equals(g.key) && !"fc_sigmark".equals(g.key)) continue;
            int add = Math.max(1, g.amount);
            grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount + add, g.item));
            d.addPeriodCount(latch, "all", 1);
            return true;
        }
        return false;
    }
}
