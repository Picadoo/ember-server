package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7905: breach place → start radius +{@link #EXTRA} once per run.
 * Zero breachRadius table. Not HoldRadius densify.
 */
public final class EmberBreachStartBoost {

    public static final double EXTRA = 1.0;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberBreachStartBoost() {}

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
