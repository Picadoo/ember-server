package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D6465: auto-claim claimable codex stage coins (rising-edge).
 * Zero stage table change. Not CodexPath densify / ConfirmTokens.
 */
public final class EmberCodexBreak {

    private EmberCodexBreak() {}

    /** @return stages claimed this call */
    public static int maybeAuto(Player p, PlayerData d, EmberRunService runs) {
        if (p == null || d == null || runs == null) return 0;
        if (EmberCodex.claimable(d).isEmpty()) return 0;
        int n = runs.claimCodexStages(p);
        if (n > 0) {
            p.sendMessage(EmberRunService.P + "§a图录自动领 · §f" + n + " §7档阶段币（零改表）");
            try {
                p.sendActionBar("§a图录 · 自动领 §f" + n + "档");
            } catch (Throwable ignored) { }
        }
        return n;
    }

    public static void schedule(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = (CoreRpgPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null || pl.getEmberRuns() == null) return;
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (!p.isOnline()) return;
            PlayerData d = pl.getDataStore().get(p.getUniqueId());
            maybeAuto(p, d, pl.getEmberRuns());
        }, 70L);
    }
}
