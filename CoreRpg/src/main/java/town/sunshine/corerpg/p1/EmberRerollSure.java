package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D4833/D4929: when afPity ≥ rules.pity, reroll preview auto-confirms if mats + gate ready.
 * Zero pity threshold / pool / shard cost change. Not forge sure ConfirmTokens densify.
 */
public final class EmberRerollSure {
    private EmberRerollSure() {}

    public static boolean forced(int pity, int need) {
        return need > 0 && pity >= need;
    }

    /** Inventory shards + coin (+ dup when dup mode). Brand/gate checked by caller. */
    public static boolean matsOk(Player p, PlayerData d, boolean dup, boolean haveDup,
                                 int needShard, int coin, CoreRpgPlugin plugin) {
        if (p == null || d == null) return false;
        if (d.getCoin() < coin) return false;
        if (dup) return haveDup;
        int haveSh = 0;
        try {
            if (plugin != null && plugin.getNiBridge() != null)
                haveSh = plugin.getNiBridge().countInInventory(p, EmberUpgradeRules.MAT_SHARD);
        } catch (Throwable ignored) { }
        return haveSh >= needShard;
    }

    /** Brand charges left for forge-shared reroll. */
    public static boolean brandOk(PlayerData d, EmberItemData t, EmberGrowthService g) {
        if (d == null || t == null || g == null) return false;
        return EmberBrandRules.chargesLeft(g.afcUsedPublic(d, t)) > 0;
    }
}
