package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D8065: first frost slow of a run → duration ×{@link #MULT}.
 * Zero frostTick table. Not RegenWindow densify.
 */
public final class EmberFrostSlowSoft {

    public static final double MULT = 0.6;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberFrostSlowSoft() {}

    /** @return ticks after soft (unchanged if not consumed). */
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
