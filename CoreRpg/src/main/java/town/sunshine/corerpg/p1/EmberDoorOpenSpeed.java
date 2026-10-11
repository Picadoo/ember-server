package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * D7649: first door open of a run → Speed I {@link #SPEED_TICKS} for online participants.
 * Zero skill_mult. Not RoomClearHeal/BossBrace densify.
 */
public final class EmberDoorOpenSpeed {

    public static final int SPEED_TICKS = 40; // 2s

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberDoorOpenSpeed() {}

    public static void maybeSpeed(EmberRunSession s, Iterable<Player> here) {
        if (s == null || s.runId == null || here == null) return;
        if (USED.putIfAbsent(s.runId, Boolean.TRUE) != null) return;
        for (Player p : here) {
            if (p == null || !p.isOnline() || p.isDead()) continue;
            if (!EmberMode.isP1(p)) continue;
            try {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_TICKS, 0, false, true), true);
                p.sendMessage(EmberRunService.P + "§b门开疾步 · 速度 I §f" + (SPEED_TICKS / 20) + "§bs（本局首次开门）");
                try { p.sendActionBar("§b门开疾步 · 速度I"); } catch (Throwable ignored) { }
            } catch (Throwable ignored) { }
        }
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
