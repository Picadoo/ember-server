package town.sunshine.corerpg.p1;

/**
 * Damage-ratio charge estimation (source table F03 / 实测计划): Bukkit 1.12 resets the attack
 * cooldown before EntityDamageByEntityEvent fires, so the charge c is recovered from the vanilla
 * melee value instead:
 *
 * <pre>
 *   raw = A × (0.2 + 0.8c²) × k + E × c      k = 1.5 for a vanilla jump-crit (only possible when c &gt; 0.9), else 1
 * </pre>
 * A is the attacker's GENERIC_ATTACK_DAMAGE attribute value, E the enchant bonus
 * (Sharpness L → 0.5L + 0.5; Smite/Bane vs matching mobs are unknown → {@code unreliable}).
 * Solved as the positive root of 0.8Ak·c² + E·c + (0.2Ak − raw) = 0.
 * Pure Java; the Bukkit side only gathers the inputs.
 */
public final class ChargeEstimator {

    private ChargeEstimator() {}

    /** Tolerance on c above 1 (float rounding in vanilla's float math). */
    static final double EPS = 1e-3;

    public static final class Result {
        public final double charge;
        public final boolean vanillaCrit;
        /** True when the inputs cannot pin c down; the caller must not grant a crit or a set count. */
        public final boolean unreliable;
        public final String note;

        Result(double charge, boolean vanillaCrit, boolean unreliable, String note) {
            this.charge = charge; this.vanillaCrit = vanillaCrit; this.unreliable = unreliable; this.note = note;
        }

        @Override public String toString() {
            return String.format("c=%.3f%s%s%s", charge, vanillaCrit ? " jump" : "", unreliable ? " UNRELIABLE" : "",
                    note == null ? "" : " (" + note + ")");
        }
    }

    /** Sharpness enchant bonus at full charge, 1.12: 0.5 × level + 0.5 (0 for level 0). */
    public static double sharpnessBonus(int level) { return level <= 0 ? 0.0 : 0.5 * level + 0.5; }

    /** Forward model, used by tests and debug output. */
    public static double vanillaMelee(double attr, double enchant, double c, boolean jumpCrit) {
        c = EmberFormula.clampCharge(c);
        double v = attr * (0.2 + 0.8 * c * c);
        if (jumpCrit && c > 0.9) v *= 1.5;
        return v + enchant * c;
    }

    /**
     * @param raw            event BASE damage at LOWEST (before any plugin touched it)
     * @param attr           attacker GENERIC_ATTACK_DAMAGE value
     * @param enchant        known enchant bonus at full charge (Sharpness)
     * @param jumpPossible   attacker state allowed a vanilla jump-crit (falling, airborne, not sprinting, …)
     * @param unknownEnchant item carries Smite/Bane of Arthropods (target-dependent bonus not modelled)
     */
    public static Result estimate(double raw, double attr, double enchant, boolean jumpPossible, boolean unknownEnchant) {
        if (!(raw >= 0) || Double.isInfinite(raw) || !(attr > 0) || Double.isInfinite(attr) || !(enchant >= 0)) {
            return new Result(0.0, false, true, "bad input");
        }
        if (jumpPossible) {
            double c = solve(raw, attr * 1.5, enchant);
            if (c > 0.9 && c <= 1.0 + EPS) {
                return new Result(Math.min(1.0, c), true, unknownEnchant, unknownEnchant ? "smite/bane" : null);
            }
        }
        double c = solve(raw, attr, enchant);
        if (Double.isNaN(c)) return new Result(0.0, false, true, "no root");
        if (c > 1.0 + EPS) {
            // More damage than a full vanilla swing can produce: some unknown source added base damage.
            return new Result(1.0, false, true, "raw above full swing");
        }
        return new Result(EmberFormula.clampCharge(c), false, unknownEnchant, unknownEnchant ? "smite/bane" : null);
    }

    /** Positive root of 0.8a·c² + e·c + (0.2a − raw) = 0; 0 when raw is below the zero-charge floor. */
    static double solve(double raw, double a, double e) {
        double qa = 0.8 * a, qb = e, qc = 0.2 * a - raw;
        if (qc >= 0) return 0.0; // raw ≤ 0.2a → c = 0
        double disc = qb * qb - 4 * qa * qc;
        if (disc < 0) return Double.NaN;
        return (-qb + Math.sqrt(disc)) / (2 * qa);
    }
}
