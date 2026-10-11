package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7873: hold event → radius +{@link #EXTRA} once per run.
 * Zero holdRadius table. Not ChainNeed densify.
 */
public final class EmberHoldRadiusBoost {

    public static final double EXTRA = 0.5;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberHoldRadiusBoost() {}

    public static double consumeExtra(String runId) {
        if (runId == null) return 0.0;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return 0.0;
        return EXTRA;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
