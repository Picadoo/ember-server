package town.sunshine.corerpg.p1;

import org.bukkit.Sound;

/**
 * D3641: 烬斩 cast-time telegraph densify + shape sfx (zero skill_mult / not Shape-native land / not Boss telegraph).
 */
public final class EmberSlashCastFeel {

    private EmberSlashCastFeel() {}

    public static int lineSamples() {
        return 9; // was 6
    }

    public static int ringPoints() {
        return 24; // was 16
    }

    public static int fanRayDegSpan() {
        return 50; // was ±40
    }

    public static int fanRays() {
        return 5; // was 3 (-50,-25,0,25,50)
    }

    public static int fanDepth() {
        return 5; // was 4
    }

    public static int particlesPerPoint(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return 4;
        if (shapeId == EmberSkillKit.SHAPE_RING) return 3;
        return 3;
    }

    public static Sound castSound(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return Sound.ENTITY_ARROW_SHOOT;
        if (shapeId == EmberSkillKit.SHAPE_RING) return Sound.ENTITY_PLAYER_ATTACK_SWEEP;
        return Sound.ENTITY_PLAYER_ATTACK_WEAK;
    }

    public static float castPitch(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return 1.4f;
        if (shapeId == EmberSkillKit.SHAPE_RING) return 0.85f;
        return 1.1f;
    }

    /** Fan ray degrees centered on look; length = fanRays(). */
    public static int[] fanDegrees() {
        int n = fanRays();
        int span = fanRayDegSpan();
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = (int) Math.round(-span + (2.0 * span) * i / (n - 1));
        }
        return out;
    }
}
