package town.sunshine.corerpg.p1;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * D7329: first non-last room clear of a run → heal {@link #HEAL_PCT} max HP for online committed.
 * Zero skill_mult / potion table. Not SetLand/Shield densify.
 */
public final class EmberRoomClearHeal {

    public static final double HEAL_PCT = 0.025;

    private static final ConcurrentHashMap<String, Boolean> USED = new ConcurrentHashMap<String, Boolean>();

    private EmberRoomClearHeal() {}

    public static boolean used(String runId) {
        return runId != null && Boolean.TRUE.equals(USED.get(runId));
    }

    /** Heal all online committed once per run. */
    public static void maybeHealRun(EmberRunSession s) {
        if (s == null || s.runId == null) return;
        if (USED.putIfAbsent(s.runId, Boolean.TRUE) != null) return;
        for (UUID u : s.committed) {
            if (s.left.contains(u)) continue;
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || !EmberMode.isP1(p)) continue;
            double heal = Math.max(0.5, p.getMaxHealth() * HEAL_PCT);
            p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + heal));
            try {
                p.sendMessage(EmberRunService.P + "§a清房回气 · 回复 §f" + String.format(java.util.Locale.ROOT, "%.1f", heal)
                        + "§a（本局首次清房）");
                p.sendActionBar("§a清房回气 · +" + String.format(java.util.Locale.ROOT, "%.1f", heal));
            } catch (Throwable ignored) { }
        }
    }

    public static void clear(String runId) {
        if (runId != null) USED.remove(runId);
    }

    public static void clearAll() { USED.clear(); }
}
