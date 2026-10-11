package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D8081: first jailer root of a run → ticks ×{@link #MULT}.
 * Zero jailerRoot table. Not FrostSlow densify.
 */
public final class EmberJailerRootSoft {

    public static final double MULT = 0.7;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberJailerRootSoft() {}

    public static int consumeTicks(String runId, int baseTicks) {
        if (runId == null || baseTicks <= 0) return baseTicks;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return baseTicks;
        return Math.max(1, (int) Math.floor(baseTicks * MULT));
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
