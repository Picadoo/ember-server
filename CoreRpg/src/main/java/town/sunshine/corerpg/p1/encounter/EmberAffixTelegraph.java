package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Particle;

/**
 * D3473: affix telegraph color-diff only (zero skill_mult).
 * Uses the affix idle fx palette — no Boss urgency / density / inside-Slow twin.
 */
public final class EmberAffixTelegraph {

    private EmberAffixTelegraph() {}

    /** Outline particle for an affix warn; unknown → FLAME. */
    public static Particle warnParticle(String affixId) {
        if (affixId == null || affixId.isEmpty()) return Particle.FLAME;
        AffixBehavior b = EmberAffixes.of(affixId);
        if (b == null) return Particle.FLAME;
        try {
            return Particle.valueOf(b.fx());
        } catch (Throwable t) {
            return Particle.FLAME;
        }
    }

    /** True when two affixes resolve to different telegraph particles (tests). */
    public static boolean differs(String a, String b) {
        return warnParticle(a) != warnParticle(b);
    }
}
