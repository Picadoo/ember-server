package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D8033: regen channel window +{@link #EXTRA_MS} once per run (easier interrupt).
 * Zero regenInterruptWindow table. Not MoltenWarn densify.
 */
public final class EmberRegenWindowBoost {

    public static final long EXTRA_MS = 400L;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberRegenWindowBoost() {}

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
