package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7777: unscathed event → +{@link #EXTRA} hit budget once per run.
 * Zero unscathedHits table. Not EventStartBrace densify.
 */
public final class EmberUnscathedBudgetBoost {

    public static final int EXTRA = 1;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberUnscathedBudgetBoost() {}

    /** @return extra budget (0 if already used). */
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
