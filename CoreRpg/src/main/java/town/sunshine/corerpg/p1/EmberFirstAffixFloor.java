package town.sunshine.corerpg.p1;

/**
 * D6945: first affix install (empty slot) soft-floors tier to {@link #FLOOR} (capped by quality cap).
 * Zero pity threshold / pool / LockNear densify.
 */
public final class EmberFirstAffixFloor {

    public static final int FLOOR = 2;

    private EmberFirstAffixFloor() {}

    public static EmberAffix.Roll apply(EmberAffix.Roll r, int oldEnc, int quality, EmberAffix.Rules rules) {
        if (r == null || rules == null || oldEnc > 0) return r;
        int cap = rules.cap(quality);
        int floor = Math.min(FLOOR, Math.max(1, cap));
        if (r.tier >= floor) return r;
        int pityAfter = r.tier >= cap ? 0 : r.pityAfter;
        if (floor >= cap) pityAfter = 0;
        return new EmberAffix.Roll(r.id, floor, pityAfter, r.forced);
    }

    public static boolean wouldFloor(int oldEnc, int quality, EmberAffix.Rules rules) {
        if (oldEnc > 0 || rules == null) return false;
        int cap = rules.cap(quality);
        return Math.min(FLOOR, Math.max(1, cap)) > 1 || cap >= 1;
    }
}
