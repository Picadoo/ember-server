package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D9937: first lock-reroll of the day → lockShard ×{@link #MULT}.
 * Zero lock_shard table. Not RerollCoinSoft densify (lock shard only).
 */
public final class EmberRerollLockShardSoft {

    public static final String C_USED = "p1_reroll_lock_shard_soft";
    public static final double MULT = 0.5;

    private EmberRerollLockShardSoft() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d);
    }

    public static int apply(PlayerData d, int lockShard, boolean consume) {
        if (lockShard <= 0 || !ready(d)) return lockShard;
        int soft = Math.max(1, (int) Math.floor(lockShard * MULT));
        if (consume) d.addPeriodCount(C_USED, "day", 1);
        return soft;
    }
}
