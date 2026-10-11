package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D6721: after {@link #NEED} consecutive in-run parry misses, next telegraph window +{@link #BONUS_MS}ms
 * once per uid@day. Zero base PARRY_WINDOW_MS / CD / flat. Not PostureGeom densify.
 */
public final class EmberParryMissBreak {

    public static final int NEED = 3;
    public static final long BONUS_MS = 80L;
    public static final String C_STREAK = "p1_parry_miss_streak";
    public static final String C_USED = "p1_parry_miss_rest";

    private EmberParryMissBreak() {}

    public static int streak(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_STREAK, "all"));
    }

    public static void onMiss(PlayerData d) {
        if (d == null) return;
        d.addPeriodCount(C_STREAK, "all", 1);
    }

    public static void onHit(PlayerData d) {
        if (d == null) return;
        int s = streak(d);
        if (s > 0) d.addPeriodCount(C_STREAK, "all", -s);
    }

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    /** @return bonus ms to add this open (0 if not ready). Consumes day latch + clears streak. */
    public static long consumeBonus(PlayerData d) {
        if (d == null || usedToday(d)) return 0L;
        if (streak(d) < NEED) return 0L;
        d.addPeriodCount(C_USED, "day", 1);
        onHit(d); // clear streak
        return BONUS_MS;
    }

    public static boolean ready(PlayerData d) {
        return d != null && !usedToday(d) && streak(d) >= NEED;
    }
}
