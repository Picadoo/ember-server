package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D11809: first locked reroll of the day → if rolled tier &lt; old tier, floor to old tier.
 * Zero pity / pool / lock_shard table. Not LockNearCap / FirstAffixFloor / RerollLockShardSoft densify.
 */
public final class EmberLockTierFloor {

    public static final String C_USED = "p1_lock_tier_floor";

    private EmberLockTierFloor() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static EmberAffix.Roll apply(EmberAffix.Roll r, int oldEnc, int quality,
                                       EmberAffix.Rules rules, boolean lock, PlayerData d) {
        if (!lock || r == null || rules == null || d == null || !ready(d)) return r;
        int oldT = EmberAffix.decodeTier(oldEnc);
        if (oldT <= 0) return r;
        int cap = rules.cap(quality);
        int floor = Math.min(oldT, Math.max(1, cap));
        if (r.tier >= floor) return r;
        d.addPeriodCount(C_USED, "day", 1);
        int pityAfter = floor >= cap ? 0 : r.pityAfter;
        return new EmberAffix.Roll(r.id, floor, pityAfter, true);
    }

    public static boolean wouldFloor(int oldEnc, int quality, EmberAffix.Rules rules,
                                     boolean lock, PlayerData d) {
        if (!lock || rules == null || !ready(d)) return false;
        int oldT = EmberAffix.decodeTier(oldEnc);
        if (oldT <= 0) return false;
        int cap = rules.cap(quality);
        return Math.min(oldT, Math.max(1, cap)) > 0;
    }
}
