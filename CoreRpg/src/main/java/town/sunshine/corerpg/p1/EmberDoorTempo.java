package town.sunshine.corerpg.p1;

import java.util.UUID;

import town.sunshine.corerpg.PlayerData;

/**
 * D3345: room-door tempo feel after clear (zero skill_mult).
 * Not Gate Speed/Resist land twin; not Boss telegraph LAVA twin.
 * Bukkit-free helpers for tests.
 */
public final class EmberDoorTempo {

    /** ASK path shortens natural door_delay (AUTO still force-opens separately). */
    public static final double ASK_DELAY_SCALE = 0.85;

    private EmberDoorTempo() {}

    /**
     * Scale applied to room door_delay when arming pending open.
     * AUTO presence does not change scale here (Gate force-open handles it).
     * Any ASK → {@link #ASK_DELAY_SCALE}; else 1.0.
     */
    public static double delayScale(EmberRunSession s, EmberRunService runs) {
        if (s == null || runs == null || s.committed == null) return 1.0;
        boolean anyAsk = false;
        for (UUID u : s.committed) {
            PlayerData d = runs.dataOf(u);
            int path = EmberGatePath.get(d);
            if (EmberGatePath.shouldAuto(path)) continue; // force-open path; ignore scale
            if (path == EmberGatePath.ASK) anyAsk = true;
        }
        return anyAsk ? ASK_DELAY_SCALE : 1.0;
    }

    public static int secondsLeft(long nowMs, long doorOpenAtMs) {
        long left = doorOpenAtMs - nowMs;
        if (left <= 0) return 0;
        return (int) Math.ceil(left / 1000.0);
    }

    public static String actionBar(int secLeft) {
        if (secLeft <= 0) return "§a门扇 · §f开启";
        return "§e门扇 · §f" + secLeft + "s";
    }

    /** MUTE gate path skips door countdown ActionBar. */
    public static boolean showCountdown(int gatePath) {
        return !EmberGatePath.shouldMute(gatePath);
    }
}
