package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * D7393: boss spawn → Resist I {@link #RESIST_TICKS} for online committed once per run.
 * Zero skill_mult / damage table. Not RoomClearHeal densify.
 */
public final class EmberBossSpawnBrace {

    public static final int RESIST_TICKS = 40; // 2s

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberBossSpawnBrace() {}

    public static void maybeBrace(EmberRunSession s) {
        if (s == null || s.runId == null) return;
        if (USED.putIfAbsent(s.runId, Boolean.TRUE) != null) return;
        for (UUID u : s.committed) {
            if (s.left.contains(u)) continue;
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || !EmberMode.isP1(p)) continue;
            try {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, RESIST_TICKS, 0, false, true), true);
                p.sendMessage(EmberRunService.P + "§b首领对峙 · 抗性 I §f" + (RESIST_TICKS / 20) + "§bs（本局一次）");
                try { p.sendActionBar("§b首领对峙 · 抗性I"); } catch (Throwable ignored) { }
            } catch (Throwable ignored) { }
        }
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
