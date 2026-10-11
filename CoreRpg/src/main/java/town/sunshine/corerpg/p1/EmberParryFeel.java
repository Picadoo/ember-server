package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * D3681: 招架 cast feel — window open ring + success densify + miss clang.
 * Zero flat/CD/windowMs/stun (not Counter twin / not Boss telegraph).
 */
public final class EmberParryFeel {

    private EmberParryFeel() {}

    public static int openRingPoints() {
        return 12;
    }

    public static double openRingRadius() {
        return 0.85;
    }

    public static int successPrimary() {
        return 28; // was 18
    }

    public static int successSecondary() {
        return 10;
    }

    public static void pulseOpen(Player player) {
        if (player == null || player.getWorld() == null) return;
        World w = player.getWorld();
        Location base = player.getLocation().clone().add(0, 0.12, 0);
        try {
            int n = openRingPoints();
            double r = openRingRadius();
            for (int i = 0; i < n; i++) {
                double rad = Math.toRadians(i * (360.0 / n));
                Location p = base.clone().add(Math.cos(rad) * r, 0, Math.sin(rad) * r);
                w.spawnParticle(Particle.CRIT_MAGIC, p, 1, 0.01, 0.01, 0.01, 0.0);
            }
            w.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 0.35f, 1.65f);
        } catch (Throwable ignored) { }
    }

    public static void pulseSuccess(Player player, LivingEntity boss) {
        if (player == null || player.getWorld() == null) return;
        World w = player.getWorld();
        Location at = boss != null && boss.isValid()
                ? boss.getLocation().clone().add(0, 1.2, 0)
                : player.getLocation().clone().add(0, 1.0, 0);
        try {
            w.spawnParticle(Particle.CRIT_MAGIC, at, successPrimary(), 0.45, 0.55, 0.45, 0.02);
            w.spawnParticle(Particle.CRIT, at, successSecondary(), 0.3, 0.35, 0.3, 0.01);
            // small shield arc around player
            Location base = player.getLocation().clone().add(0, 1.0, 0);
            for (int i = 0; i < 8; i++) {
                double rad = Math.toRadians(i * 45);
                Location p = base.clone().add(Math.cos(rad) * 0.7, 0, Math.sin(rad) * 0.7);
                w.spawnParticle(Particle.SMOKE_NORMAL, p, 2, 0.02, 0.05, 0.02, 0.0);
            }
            w.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.55f, 1.6f);
            w.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.5f, 1.25f);
        } catch (Throwable ignored) { }
    }

    public static void pulseMiss(Player player) {
        if (player == null || player.getWorld() == null) return;
        try {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.25f, 0.7f);
            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL,
                    player.getLocation().add(0, 1.0, 0), 6, 0.2, 0.2, 0.2, 0.01);
        } catch (Throwable ignored) { }
    }
}
