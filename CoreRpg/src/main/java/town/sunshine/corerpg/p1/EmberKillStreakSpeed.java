package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * D8193: {@link #NEED} room-mob kills in a run → Speed I {@link #SPEED_TICKS} once per run.
 * Zero skill_mult. Not DoorOpen/EventBrace densify.
 */
public final class EmberKillStreakSpeed {

    public static final int NEED = 5;
    public static final int SPEED_TICKS = 40; // 2s

    private static final ConcurrentHashMap<String, Integer> KILLS = new ConcurrentHashMap<String, Integer>();
    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberKillStreakSpeed() {}

    public static void noteKill(String runId, Iterable<Player> here) {
        if (runId == null || here == null) return;
        if (USED.containsKey(runId)) return;
        int n = 0;
        Integer cur = KILLS.get(runId);
        if (cur != null) n = cur.intValue();
        n++;
        KILLS.put(runId, Integer.valueOf(n));
        if (n < NEED) return;
        if (USED.putIfAbsent(runId, Boolean.TRUE) != null) return;
        for (Player p : here) {
            if (p == null || !p.isOnline() || p.isDead()) continue;
            if (!EmberMode.isP1(p)) continue;
            try {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_TICKS, 0, false, true), true);
                p.sendMessage(EmberRunService.P + "§b连杀疾步 · 速度 I §f" + (SPEED_TICKS / 20) + "§bs（本局 " + NEED + " 杀）");
                try { p.sendActionBar("§b连杀疾步"); } catch (Throwable ignored) { }
            } catch (Throwable ignored) { }
        }
    }

    public static void clear(String runId) {
        if (runId == null) return;
        KILLS.remove(runId);
        USED.remove(runId);
    }

    public static void clearAll() { KILLS.clear(); USED.clear(); }
}
