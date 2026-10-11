package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7841: chain event → need −{@link #LESS} (min 1) once per run.
 * Zero chainNeed table. Not Unscathed/Crystal densify.
 */
public final class EmberChainNeedSoft {

    public static final int LESS = 1;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberChainNeedSoft() {}

    /** @return amount to subtract (0 if already used). */
    public static int consumeLess(String runId) {
        if (runId == null) return 0;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return 0;
        return LESS;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
