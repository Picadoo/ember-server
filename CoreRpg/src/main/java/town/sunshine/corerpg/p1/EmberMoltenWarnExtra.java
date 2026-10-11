package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7969: molten schedule → warn/boom +{@link #EXTRA_MS} once per run.
 * Zero moltenDelay/Warn table. Not Breach/Timed densify.
 */
public final class EmberMoltenWarnExtra {

    public static final long EXTRA_MS = 400L;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberMoltenWarnExtra() {}

    public static long consumeExtraMs(String runId) {
        if (runId == null) return 0L;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return 0L;
        return EXTRA_MS;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
