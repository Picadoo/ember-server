package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D7201: successful 招架 arms next 烬斩 range +{@link #BONUS} once.
 * Zero skill_mult / ParryMiss densify / StepSlash densify.
 */
public final class EmberParrySlashArm {

    public static final double BONUS = 0.30;

    private static final ConcurrentHashMap<UUID, Boolean> ARMED = new ConcurrentHashMap<UUID, Boolean>();

    private EmberParrySlashArm() {}

    public static void arm(UUID id) {
        if (id != null) ARMED.put(id, Boolean.TRUE);
    }

    public static boolean isArmed(UUID id) {
        return id != null && Boolean.TRUE.equals(ARMED.get(id));
    }

    public static double consume(UUID id) {
        if (id == null) return 0.0;
        if (!Boolean.TRUE.equals(ARMED.remove(id))) return 0.0;
        return BONUS;
    }

    public static void clear(UUID id) {
        if (id != null) ARMED.remove(id);
    }
}
