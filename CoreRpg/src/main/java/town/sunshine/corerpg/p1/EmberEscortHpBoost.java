package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

/**
 * D7953: escort rabbit → HP ×{@link #MULT} once per run.
 * Zero escortHp table. Not BeaconHp densify (rabbit, not lantern).
 */
public final class EmberEscortHpBoost {

    public static final double MULT = 1.2;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberEscortHpBoost() {}

    public static double consumeMult(String runId) {
        if (runId == null) return 1.0;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return 1.0;
        return MULT;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
