package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D6433: soft pity for signature stamp — when dry streak ≥ expectClears, force stamp.
 * Zero {@link EmberSignature#stampRate()} change. Not sure-do densify.
 */
public final class EmberSigStampPity {

    private EmberSigStampPity() {}

    /** NEED mirrors expected clears at live rate (ceil 1/rate). */
    public static int need() {
        return Math.max(1, EmberSigStampDry.expectClears());
    }

    public static boolean ready(PlayerData d) {
        return EmberSigStampDry.get(d) >= need();
    }
}
