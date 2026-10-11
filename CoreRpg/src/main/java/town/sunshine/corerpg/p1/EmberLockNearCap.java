package town.sunshine.corerpg.p1;

/**
 * D6689: lock-reroll when current tier is cap−1 → force cap tier.
 * Zero pity threshold / pool / STAMP_RATE change. Not sure-do densify.
 */
public final class EmberLockNearCap {

    private EmberLockNearCap() {}

    public static EmberAffix.Roll apply(EmberAffix.Roll r, int oldEnc, int quality,
                                       EmberAffix.Rules rules, boolean lock) {
        if (!lock || r == null || rules == null || r.forced) return r;
        int cap = rules.cap(quality);
        if (cap <= 1) return r;
        int oldT = Math.min(EmberAffix.decodeTier(oldEnc), cap);
        if (oldT != cap - 1) return r;
        if (r.tier >= cap) return r;
        return new EmberAffix.Roll(r.id, cap, 0, true);
    }

    public static boolean wouldForce(int oldEnc, int quality, EmberAffix.Rules rules, boolean lock) {
        if (!lock || rules == null) return false;
        int cap = rules.cap(quality);
        if (cap <= 1) return false;
        return Math.min(EmberAffix.decodeTier(oldEnc), cap) == cap - 1;
    }
}
