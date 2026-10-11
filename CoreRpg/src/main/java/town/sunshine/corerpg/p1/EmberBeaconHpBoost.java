package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7745: beacon event place → HP ×{@link #MULT} once per run.
 * Zero beaconHp table raise. Not EventStartBrace densify.
 */
public final class EmberBeaconHpBoost {

    public static final double MULT = 1.2;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberBeaconHpBoost() {}

    /** @return multiplier to apply (1.0 if already used). */
    public static double consumeMult(String runId) {
        if (runId == null) return 1.0;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return 1.0;
        return MULT;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
