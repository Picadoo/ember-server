package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D7553: first raid revive of a run for a player → {@link #BOOST_PCT} max HP (else base 50%).
 * Zero skill_mult. Not BossSpawnBrace densify.
 */
public final class EmberRaidReviveBoost {

    public static final double BOOST_PCT = 0.65;
    public static final double BASE_PCT = 0.50;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberRaidReviveBoost() {}

    private static String key(String runId, UUID u) {
        return runId + ":" + u;
    }

    /** @return heal fraction to apply. */
    public static double healPct(String runId, UUID u) {
        if (runId == null || u == null) return BASE_PCT;
        if (USED.putIfAbsent(key(runId, u), Boolean.TRUE) != null) return BASE_PCT;
        return BOOST_PCT;
    }

    public static void clearRun(String runId) {
        if (runId == null) return;
        String prefix = runId + ":";
        USED.keySet().removeIf(k -> k.startsWith(prefix));
    }

    public static void clearAll() { USED.clear(); }
}
