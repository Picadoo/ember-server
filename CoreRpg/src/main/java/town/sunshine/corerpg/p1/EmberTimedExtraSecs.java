package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7937: timed variety event → +{@link #EXTRA} seconds once per run.
 * Zero timedSecs table. Not BreachStart densify.
 */
public final class EmberTimedExtraSecs {

    public static final int EXTRA = 5;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberTimedExtraSecs() {}

    public static int consumeExtra(String runId) {
        if (runId == null) return 0;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return 0;
        return EXTRA;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
