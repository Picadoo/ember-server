package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D6113: upgrade preview→do only when weekly forgegoal is UPGRADE (player committed).
 * Zero price / gate / directed-forge table change. Not blanket upgrade sure.
 */
public final class EmberUpgradeSure {
    private EmberUpgradeSure() {}

    public static boolean goalReady(PlayerData d) {
        return d != null && EmberForgeGoal.get(d) == EmberForgeGoal.UPGRADE;
    }
}
