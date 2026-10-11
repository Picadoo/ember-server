package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D4769/D4897: attune unlock auto when marks ≥ ALT and alt not unlocked (chase-first pick).
 * Zero ALT_MARKS raise / ConfirmTokens densify (toggle stays manual). Not forge/Marks twin.
 */
public final class EmberAttuneBreak {

    /** Latch: p1_attune_auto_<id>@all — one auto unlock per signature id */
    public static final String C_AUTO = "p1_attune_auto_";

    private EmberAttuneBreak() {}

    public static boolean townOk(Player p, EmberRunService runs) {
        if (p == null || runs == null) return false;
        if (EmberMode.isP1World(p.getWorld()) || runs.isRunWorld(p.getWorld())) return false;
        return EmberAssetGuard.hold(p) == null;
    }

    public static boolean maybeAuto(Player p, PlayerData d, EmberGrowthService g) {
        if (p == null || d == null || g == null) return false;
        EmberRunService runs = g.runsPublic();
        if (!townOk(p, runs)) return false;
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
        // D4897: one delayed scan for a *different* ready id (no same-tick mark burn chain)
        scheduleNextScan(p);
        return true;
    }

    static void scheduleNextScan(Player p) {
        if (p == null) return;
        final java.util.UUID id = p.getUniqueId();
        CoreRpgPlugin pl = (CoreRpgPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) return;
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            Player q = Bukkit.getPlayer(id);
            if (q == null || !q.isOnline()) return;
            EmberGrowthService g = EmberGrowthService.get();
            if (g == null) return;
            PlayerData xd = g.dataPublic(id);
            if (xd == null) return;
            g.maybeAttuneReadyCue(q, xd);
        }, 40L);
    }
}
