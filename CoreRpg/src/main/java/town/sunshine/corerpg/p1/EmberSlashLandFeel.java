package town.sunshine.corerpg.p1;

import org.bukkit.Sound;

/**
 * D3721: 烬斩 land burst densify + shape sfx (not cast-telegraph twin / not Shape-native land).
 */
public final class EmberSlashLandFeel {

    private EmberSlashLandFeel() {}

    public static int lineSamples() { return 9; }
    public static int linePpt() { return 6; }
    public static int ringPoints() { return 18; }
    public static int ringPpt() { return 3; }
    public static int fanArms() { return 11; } // was 9 (-4..4)
    public static int fanPpt() { return 4; }

    public static Sound landSound(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return Sound.ENTITY_ARROW_HIT;
        if (shapeId == EmberSkillKit.SHAPE_RING) return Sound.ENTITY_PLAYER_ATTACK_CRIT;
        return Sound.ENTITY_PLAYER_ATTACK_STRONG;
    }

    public static float landPitch(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return 1.35f;
        if (shapeId == EmberSkillKit.SHAPE_RING) return 0.9f;
        return 1.05f;
    }
}
