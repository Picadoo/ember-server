package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D8001: relay wrong later mark → skip-forgive once per run (advance to that index).
 * Zero relayCount table. Not Hold/Breach densify.
 */
public final class EmberRelayForgive {

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberRelayForgive() {}

    /** @return true if this run may forgive once (consumes). */
    public static boolean consume(String runId) {
        if (runId == null) return false;
        return USED.putIfAbsent(runId, Boolean.TRUE) == null;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
