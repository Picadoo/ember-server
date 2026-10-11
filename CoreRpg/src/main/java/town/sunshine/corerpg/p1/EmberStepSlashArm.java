package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D6977: successful 踏步 arms next 烬斩 range +{@link #BONUS} once.
 * Zero skill_mult / DashStep densify (that is 烬突→踏步).
 */
public final class EmberStepSlashArm {

    public static final double BONUS = 0.25;

    private static final ConcurrentHashMap<UUID, Boolean> ARMED = new ConcurrentHashMap<UUID, Boolean>();

    private EmberStepSlashArm() {}

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
