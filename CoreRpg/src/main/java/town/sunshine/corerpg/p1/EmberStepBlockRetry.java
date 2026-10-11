package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import town.sunshine.corerpg.PlayerData;

/**
 * D7137: 踏步受阻 → arm next step travel +{@link #BONUS} once per uid@day.
 * Zero base step distance / skill_mult. Not DashStep/StepSlash densify.
 */
public final class EmberStepBlockRetry {

    public static final String C_USED = "p1_step_block_retry";
    public static final double BONUS = 0.5;

    private static final ConcurrentHashMap<UUID, Boolean> ARMED = new ConcurrentHashMap<UUID, Boolean>();

    private EmberStepBlockRetry() {}

    public static boolean usedToday(PlayerData d) {
        return d != null && d.periodCount(C_USED, "day") > 0;
    }

    /** On blocked step: if day latch free, arm bonus and consume latch. */
    public static void onBlock(PlayerData d, UUID id) {
        if (d == null || id == null || usedToday(d)) return;
        d.addPeriodCount(C_USED, "day", 1);
        ARMED.put(id, Boolean.TRUE);
    }

    public static double consume(UUID id) {
        if (id == null) return 0.0;
        if (!Boolean.TRUE.equals(ARMED.remove(id))) return 0.0;
        return BONUS;
    }

    public static boolean isArmed(UUID id) {
        return id != null && Boolean.TRUE.equals(ARMED.get(id));
    }

    public static void clear(UUID id) {
        if (id != null) ARMED.remove(id);
    }
}
