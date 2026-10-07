package town.sunshine.corerpg.p1;

/**
 * ember-v1.0-P1 unified B/H/D formulas (策划书 §7.2). Pure functions, no Bukkit types.
 *
 * <pre>
 * B  = A × (1 + e_w + q_w + f_w) + 0.2 × (L − 10)
 * H0 = 20 + h × (1 + e_c + q_c + f_c) + (L − 10)
 * H  = sustain_hp_mult × H0 (炽愈成套；D293 现行 1.00) else H0
 * M  = max(0.5, 40 / (40 + D));  EHP = H / M
 * melee = B × (0.2 + 0.8c²) × (1.5 if crit);  crit: 10%, only when c ≥ 0.9
 * 烬斩 = 1.5 × B, no crit
 * </pre>
 * The level part counts from Lv10, capped at +10 attack / +50 HP (§3, Lv60), and is outside the bracket.
 */
public final class EmberFormula {

    private EmberFormula() {}

    /** Levels above the base that count, clamped to [0, cap − base]. */
    public static int levelSteps(EmberTables t, int level) {
        int steps = level - t.levelBase;
        int max = t.levelCap - t.levelBase;
        return steps < 0 ? 0 : (steps > max ? max : steps);
    }

    public static double levelAttack(EmberTables t, int level) { return t.levelAtkPer * levelSteps(t, level); }

    public static double levelHp(EmberTables t, int level) { return t.levelHpPer * levelSteps(t, level); }

    /** Sum inside the bracket; additive, never multiplied (§7.3: 1.78, not 1.60×1.12×1.06). */
    public static double growth(EmberTables t, int quality, int craft, int enhance) {
        return 1.0 + t.enhance(enhance) + t.quality(quality) + t.craft(craft);
    }

    /**
     * Base hit B. {@code tier < 0} means no valid P1 weapon: only the level part, never below 1 (bare hand).
     */
    public static double baseAttack(EmberTables t, int tier, int quality, int craft, int enhance, int level) {
        double lv = levelAttack(t, level);
        if (tier < 0) return Math.max(1.0, lv);
        return t.weaponA(tier) * growth(t, quality, craft, enhance) + lv;
    }

    /** H0. {@code tier < 0} means no valid charm: 20 + level part. */
    public static double baseHp(EmberTables t, int tier, int quality, int craft, int enhance, int level) {
        double lv = levelHp(t, level);
        if (tier < 0) return 20.0 + lv;
        return 20.0 + t.charmH(tier) * growth(t, quality, craft, enhance) + lv;
    }

    public static double maxHp(EmberTables t, double h0, boolean sustainSet) {
        return sustainSet ? t.sustainHpMult * h0 : h0;
    }

    /** Defense only from the effective charm; quality/craft/enhance never touch D (§5.1). */
    public static double defense(EmberTables t, int charmTier) {
        return charmTier < 0 ? 0.0 : t.charmD(charmTier);
    }

    /** Damage-taken multiplier M, applied exactly once in the P1 pipeline. */
    public static double mitigation(EmberTables t, double defense) {
        if (!(defense >= 0) || Double.isInfinite(defense)) defense = 0; // NaN/negative/∞ → no defense
        return Math.max(t.defFloor, t.defK / (t.defK + defense));
    }

    public static double ehp(double maxHp, double mitigation) { return maxHp / mitigation; }

    public static double clampCharge(double c) {
        if (!(c >= 0)) return 0.0; // NaN, negative
        return c > 1.0 ? 1.0 : c;
    }

    /** Vanilla 1.12 cooldown curve, reused as the P1 charge factor: 0.2 + 0.8c². */
    public static double chargeFactor(double c) {
        c = clampCharge(c);
        return 0.2 + 0.8 * c * c;
    }

    public static boolean critEligible(EmberTables t, double c) { return clampCharge(c) >= t.critMinCharge; }

    /** Crit decision from a uniform roll in [0,1): crit iff eligible and roll &lt; rate. */
    public static boolean rollCrit(EmberTables t, double c, double roll) {
        return critEligible(t, c) && roll >= 0 && roll < t.critRate;
    }

    /** Final P1 melee value before the target's own pipeline. Never NaN/negative. */
    public static double melee(EmberTables t, double base, double c, boolean crit) {
        if (!(base > 0)) return 0.0;
        double v = base * chargeFactor(c);
        if (crit && critEligible(t, c)) v *= t.critMult;
        return v;
    }

    /** 烬斩 = 1.5B, never crits (§4.4). */
    public static double skill(EmberTables t, double base) { return base > 0 ? t.skillMult * base : 0.0; }

    /** Expected value of a full-charge swing including the 10%×1.5 crit: B × (1 + rate × (mult − 1)). */
    public static double expectedFullSwing(EmberTables t, double base) {
        return base * (1.0 + t.critRate * (t.critMult - 1.0));
    }
}
