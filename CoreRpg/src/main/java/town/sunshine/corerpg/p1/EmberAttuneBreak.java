package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D4769: attune unlock auto when marks ≥ ALT and alt not unlocked (chase-first pick).
 * Zero ALT_MARKS raise / ConfirmTokens densify (toggle stays manual). Not forge/Marks twin.
 */
public final class EmberAttuneBreak {

    /** Latch: p1_attune_auto_<id>@all — one auto unlock per signature id */
    public static final String C_AUTO = "p1_attune_auto_";

    private EmberAttuneBreak() {}

    public static boolean maybeAuto(Player p, PlayerData d, EmberGrowthService g) {
        if (p == null || d == null || g == null) return false;
        EmberSignature.Def def = g.pickAttuneReadyPublic(d);
        if (def == null) return false;
        String key = C_AUTO + def.id;
        if (d.periodCount(key, "all") > 0) return false;
        d.addPeriodCount(key, "all", 1);
        boolean ok = g.autoAttuneUnlock(p, def);
        if (!ok) {
            d.addPeriodCount(key, "all", -1);
            return false;
        }
        return true;
    }
}
