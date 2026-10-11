package town.sunshine.corerpg.p1;

import org.bukkit.Sound;

/**
 * D3617: 踏步 trail densify + land pitch (zero skill_mult / not dash twin / not Q05 dist).
 */
public final class EmberStepFeel {

    private EmberStepFeel() {}

    public static int trailPrimary(int variant) {
        if (variant == EmberSkillKit.STEP_VARIANT_HUOHEN || variant == EmberSkillKit.STEP_VARIANT_BAOSHAN) return 5;
        if (variant == EmberSkillKit.STEP_VARIANT_CHENGHU) return 4;
        return 4;
    }

    public static int trailSecondary(int variant) {
        if (variant == EmberSkillKit.STEP_VARIANT_BAOSHAN) return 3;
        return 2;
    }

    public static int trailHead(int variant) {
        if (variant == EmberSkillKit.STEP_VARIANT_HUOHEN || variant == EmberSkillKit.STEP_VARIANT_BAOSHAN) return 14;
        return 12;
    }

    /** Footprint mid-line count per sample (plain step readable). */
    public static int footprintPerSample() {
        return 1;
    }

    public static float landPitch(int variant, boolean back) {
        float p = 1.0f;
        if (variant == EmberSkillKit.STEP_VARIANT_HUOHEN) p = 0.85f;
        else if (variant == EmberSkillKit.STEP_VARIANT_BAOSHAN) p = 1.35f;
        else if (variant == EmberSkillKit.STEP_VARIANT_CHENGHU) p = 1.2f;
        if (back) p *= 0.92f;
        return p;
    }

    public static Sound landExtraSound(int variant) {
        if (variant == EmberSkillKit.STEP_VARIANT_HUOHEN) return Sound.ITEM_FIRECHARGE_USE;
        if (variant == EmberSkillKit.STEP_VARIANT_BAOSHAN) return Sound.ENTITY_PLAYER_ATTACK_SWEEP;
        if (variant == EmberSkillKit.STEP_VARIANT_CHENGHU) return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        return Sound.ENTITY_ENDERDRAGON_FLAP;
    }
}
