package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7809: crystal event → place +{@link #EXTRA} crystal once per run.
 * Zero crystalCount table. Not UnscathedBudget densify.
 */
public final class EmberCrystalExtra {

    public static final int EXTRA = 1;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberCrystalExtra() {}

    /** @return extra crystals to place (0 if already used). */
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
