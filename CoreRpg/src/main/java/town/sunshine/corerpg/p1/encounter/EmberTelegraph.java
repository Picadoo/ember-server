package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Particle;

/**
 * D3281: boss telegraph geometry readability (zero skill_mult / no stun twin).
 * Bukkit-free math helpers for unit tests; Particle constants only.
 */
public final class EmberTelegraph {

    /** Urgency threshold for denser / hotter telegraph. */
    public static final double HOT_URGENCY = 0.70;
    /** Inside-zone Slow starts at this urgency. */
    public static final double INSIDE_SLOW_URGENCY = 0.40;
    public static final int INSIDE_SLOW_TICKS = 4;

    private EmberTelegraph() {}

    /**
     * 0 at warn start → 1 at impact. {@code warnMs} ≤0 → 1.
     */
    public static double urgency(long nowMs, long pendingAtMs, long warnMs) {
        if (warnMs <= 0) return 1.0;
        long left = pendingAtMs - nowMs;
        if (left <= 0) return 1.0;
        if (left >= warnMs) return 0.0;
        return 1.0 - (left / (double) warnMs);
    }

    public static boolean hot(double urgency) {
        return urgency >= HOT_URGENCY;
    }

    public static int densityMult(double urgency) {
        return hot(urgency) ? 2 : 1;
    }

    /** Outline particle by skill type + share + urgency (hot → LAVA except share). */
    public static Particle outlineParticle(String type, boolean share, double urgency) {
        if (share) return Particle.VILLAGER_HAPPY;
        if (hot(urgency)) return Particle.LAVA;
        if (type == null) return Particle.FLAME;
        switch (type) {
            case "line": return Particle.CRIT;
            case "charge": return Particle.FLAME;
            case "circle": return Particle.FLAME;
            default: return Particle.SPELL_WITCH; // fan / cone
        }
    }

    /** Sparse fill particle (distinct from outline when cool). */
    public static Particle fillParticle(String type, boolean share, double urgency) {
        if (share) return Particle.VILLAGER_HAPPY;
        if (hot(urgency)) return Particle.FLAME;
        if ("line".equals(type) || "charge".equals(type)) return Particle.SMOKE_NORMAL;
        if ("circle".equals(type)) return Particle.SPELL_MOB;
        return Particle.CRIT_MAGIC;
    }

    public static boolean shouldInsideSlow(double urgency, boolean inside) {
        return inside && urgency >= INSIDE_SLOW_URGENCY;
    }

    /** Seconds left until impact, floored ≥0. */
    public static int secondsLeft(long nowMs, long pendingAtMs) {
        long left = pendingAtMs - nowMs;
        if (left <= 0) return 0;
        return (int) Math.ceil(left / 1000.0);
    }

    public static String insideActionBar(boolean inside, int secLeft, String typeLabel) {
        String shape = typeLabel == null || typeLabel.isEmpty() ? "预警" : typeLabel;
        if (inside) return "§c危险区 · §f" + shape + " §c· " + secLeft + "s";
        return "§a安全 · §7" + shape + " §8· " + secLeft + "s";
    }

    public static String typeLabel(String type) {
        if (type == null) return "预警";
        switch (type) {
            case "circle": return "圆";
            case "line": return "条带";
            case "charge": return "冲撞";
            default: return "扇形";
        }
    }
}
