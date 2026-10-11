package town.sunshine.corerpg.p1;

import org.bukkit.Sound;

/**
 * D3577: 烬突 trail densify + land sfx (zero skill_mult / no radius twin).
 */
public final class EmberDashFeel {

    private EmberDashFeel() {}

    public static int trailPrimary(String fam) {
        if ("burst".equals(fam) || "scorch".equals(fam)) return 7;
        if ("sustain".equals(fam)) return 5;
        return 5;
    }

    public static int trailSecondary(String fam) {
        if ("burst".equals(fam)) return 4;
        if ("scorch".equals(fam)) return 3;
        return 3;
    }

    public static int trailHead(String fam) {
        if ("burst".equals(fam) || "scorch".equals(fam)) return 16;
        return 12;
    }

    public static Sound landSound(String fam) {
        if ("burst".equals(fam)) return Sound.ENTITY_PLAYER_ATTACK_SWEEP;
        if ("sustain".equals(fam)) return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        if ("scorch".equals(fam)) return Sound.ITEM_FIRECHARGE_USE;
        return Sound.ENTITY_ENDERMEN_TELEPORT;
    }

    public static float landPitch(String fam) {
        if ("burst".equals(fam)) return 1.35f;
        if ("sustain".equals(fam)) return 1.6f;
        if ("scorch".equals(fam)) return 0.9f;
        return 1.2f;
    }
}
