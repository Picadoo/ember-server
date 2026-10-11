package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

/**
 * D4049: codex stage claimable quiet tip — no Path / ConfirmTokens.
 */
public final class EmberCodexQuiet {

    private EmberCodexQuiet() {}

    public static boolean maybeQuietTip(Player p, PlayerData d) {
        if (p == null || d == null) return false;
        if (EmberMode.isP1World(p.getWorld())) return false;
        int stage = EmberCodex.nextQuietClaimable(d);
        if (stage < 0) return false;
        EmberCodex.markQuietStage(d, stage);
        int at = EmberCodex.STAGE_AT[stage];
        int coin = EmberCodex.stageCoin(stage);
        String P = EmberRunService.P;
        p.sendMessage(P + "§a图录可领 · §f集齐" + at + "种 §7· 币 §f" + coin
                + " §8· /corerpg p1 codex claim");
        try {
            p.sendActionBar("§a图录可领 · §f" + at + "种 → 币" + coin);
        } catch (Throwable ignored) { }
        return true;
    }
}
