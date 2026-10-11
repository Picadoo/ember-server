package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D6753: successful 烬突 land arms next 踏步 travel +{@link #BONUS} once (chain feel).
 * Zero skill_mult / dash distance / MapResonance / PostureGeom densify.
 */
public final class EmberDashStepLink {

    public static final double BONUS = 0.55;

    private static final ConcurrentHashMap<UUID, Boolean> ARMED = new ConcurrentHashMap<UUID, Boolean>();

    private EmberDashStepLink() {}

    public static void arm(UUID id) {
        if (id != null) ARMED.put(id, Boolean.TRUE);
    }

    /** @return bonus to add; clears arm. */
    public static double consume(UUID id) {
        if (id == null) return 0.0;
        if (!Boolean.TRUE.equals(ARMED.remove(id))) return 0.0;
        return BONUS;
    }

    public static boolean isArmed(UUID id) {
        return id != null && Boolean.TRUE.equals(ARMED.get(id));
    }

    /** test helper */
    public static void clear(UUID id) {
        if (id != null) ARMED.remove(id);
    }
}
