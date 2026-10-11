package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D12001: first locked reroll of the day while at cap−2 → floor tier to cap−1.
 * Zero pity / pool / lock_shard. Not LockNearCap (cap−1→cap) / LockTierFloor densify.
 */
public final class EmberLockMidFloor {

    public static final String C_USED = "p1_lock_mid_floor";

    private EmberLockMidFloor() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static EmberAffix.Roll apply(EmberAffix.Roll r, int oldEnc, int quality,
                                       EmberAffix.Rules rules, boolean lock, PlayerData d) {
        if (!lock || r == null || rules == null || d == null || !ready(d) || r.forced) return r;
        int cap = rules.cap(quality);
        if (cap <= 2) return r;
        int oldT = Math.min(EmberAffix.decodeTier(oldEnc), cap);
        if (oldT != cap - 2) return r;
        int floor = cap - 1;
        if (r.tier >= floor) return r;
        d.addPeriodCount(C_USED, "day", 1);
        int pityAfter = floor >= cap ? 0 : r.pityAfter;
        return new EmberAffix.Roll(r.id, floor, pityAfter, true);
    }

    public static boolean wouldFloor(int oldEnc, int quality, EmberAffix.Rules rules,
                                     boolean lock, PlayerData d) {
        if (!lock || rules == null || !ready(d)) return false;
        int cap = rules.cap(quality);
        if (cap <= 2) return false;
        return Math.min(EmberAffix.decodeTier(oldEnc), cap) == cap - 2;
    }
}
