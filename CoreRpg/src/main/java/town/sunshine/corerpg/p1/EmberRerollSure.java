package town.sunshine.corerpg.p1;

/**
 * D4833: when afPity ≥ rules.pity, reroll preview auto-confirms if mats ready.
 * Zero pity threshold / pool / shard cost change. Not forge sure ConfirmTokens densify.
 */
public final class EmberRerollSure {
    private EmberRerollSure() {}

    public static boolean forced(int pity, int need) {
        return need > 0 && pity >= need;
    }
}
