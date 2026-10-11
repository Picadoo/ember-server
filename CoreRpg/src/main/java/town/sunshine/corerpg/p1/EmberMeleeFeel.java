package town.sunshine.corerpg.p1;

import org.bukkit.Particle;

/**
 * D3505: P1 melee hit spark by active set family (zero skill_mult).
 * Not rhythm/telegraph twin. Bukkit Particle only for tests.
 */
public final class EmberMeleeFeel {

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
}
