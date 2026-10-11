package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * D3505/D3553: P1 melee hit spark by set — fan spray + sfx (zero skill_mult).
 */
public final class EmberMeleeFeel {

    public static final double FAN_HALF_DEG = 35.0;

    private EmberMeleeFeel() {}

    public static Particle sparkParticle(String activeSet) {
        if (activeSet == null || activeSet.isEmpty() || "none".equals(activeSet)) return Particle.CRIT_MAGIC;
        if ("scorch".equals(activeSet) || "huohen".equals(activeSet)) return Particle.FLAME;
        if ("burst".equals(activeSet) || "baoshan".equals(activeSet)) return Particle.CRIT;
        if ("sustain".equals(activeSet) || "chenghu".equals(activeSet)) return Particle.HEART;
        return Particle.CRIT_MAGIC;
    }

    public static int sparkCount(boolean crit) {
        return crit ? 10 : 6;
    }

    /** D3553: fan arms each side of look (exclusive of center). */
    public static int fanArms(boolean crit) {
        return crit ? 4 : 3;
    }

    public static Sound hitSound(boolean crit) {
        return crit ? Sound.ENTITY_PLAYER_ATTACK_CRIT : Sound.ENTITY_PLAYER_ATTACK_STRONG;
    }

    /**
     * Spawn directional fan sparks at hit location toward attacker look.
     * Pure geometry feel — Bukkit side effects only when world/player non-null.
     */
    public static void spawnHitSparks(Player attacker, Location hitAt, String activeSet, boolean crit) {
        if (attacker == null || hitAt == null || hitAt.getWorld() == null) return;
        World w = hitAt.getWorld();
        Particle fx = sparkParticle(activeSet);
        Vector look = attacker.getLocation().getDirection().clone();
        look.setY(0);
        if (look.lengthSquared() < 1e-6) look = new Vector(0, 0, 1);
        look.normalize();
        Location base = hitAt.clone().add(0, 1.0, 0);
        int arms = fanArms(crit);
        double half = Math.toRadians(FAN_HALF_DEG);
        try {
            w.spawnParticle(fx, base, sparkCount(crit) / 2, 0.12, 0.15, 0.12, 0.01);
            for (int i = -arms; i <= arms; i++) {
                if (i == 0) continue;
                double a = half * i / (double) arms;
                double cos = Math.cos(a), sin = Math.sin(a);
                Vector v = new Vector(look.getX() * cos - look.getZ() * sin, 0.08,
                        look.getX() * sin + look.getZ() * cos).multiply(0.55);
                w.spawnParticle(fx, base.clone().add(v), crit ? 2 : 1, 0.02, 0.02, 0.02, 0.0);
            }
            attacker.playSound(hitAt, hitSound(crit), crit ? 0.7f : 0.45f, crit ? 1.15f : 1.0f);
        } catch (Throwable ignored) { }
    }
}
