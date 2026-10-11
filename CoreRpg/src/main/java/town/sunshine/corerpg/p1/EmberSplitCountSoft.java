package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D8321: split death-spawn → count −{@link #LESS} once per run (min 1 if base≥1).
 * Zero splitCount table. Not elite telegraph densify.
 */
public final class EmberSplitCountSoft {

    public static final int LESS = 1;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberSplitCountSoft() {}

    public static int consumeCount(String runId, int base) {
        if (runId == null || base <= 1) return base;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return base;
        return Math.max(1, base - LESS);
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
