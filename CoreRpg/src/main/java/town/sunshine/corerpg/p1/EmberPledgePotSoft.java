package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D8609: when potionCap hit → allow +{@link #EXTRA} sip once per run (lean pledge soft).
 * Zero potionCap table. Not PotSip densify (cap, not heal pct).
 */
public final class EmberPledgePotSoft {

    public static final int EXTRA = 1;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberPledgePotSoft() {}

    /** @return true if this over-cap sip is forgiven (consumes). */
    public static boolean consumeForgive(String runId) {
        if (runId == null) return false;
        return USED.putIfAbsent(runId, Boolean.TRUE) == null;
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
