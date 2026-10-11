package town.sunshine.corerpg.p1;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * D7713: first variety event start of a run → Resist I {@link #RESIST_TICKS} for online participants.
 * Zero skill_mult. Not BossSpawnBrace densify.
 */
public final class EmberEventStartBrace {

    public static final int RESIST_TICKS = 40; // 2s

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberEventStartBrace() {}

    public static void maybeBrace(EmberRunSession s, Iterable<Player> here) {
        if (s == null || s.runId == null || here == null) return;
        if (USED.putIfAbsent(s.runId, Boolean.TRUE) != null) return;
        for (Player p : here) {
            if (p == null || !p.isOnline() || p.isDead()) continue;
            if (!EmberMode.isP1(p)) continue;
            try {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, RESIST_TICKS, 0, false, true), true);
                p.sendMessage(EmberRunService.P + "§b花样开局 · 抗性 I §f" + (RESIST_TICKS / 20) + "§bs（本局首次花样）");
                try { p.sendActionBar("§b花样开局 · 抗性I"); } catch (Throwable ignored) { }
            } catch (Throwable ignored) { }
        }
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
