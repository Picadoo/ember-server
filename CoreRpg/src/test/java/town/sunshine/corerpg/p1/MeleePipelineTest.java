package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The pure part of the P1 melee path as EmberCombatListener runs it: vanilla raw → estimated c → B formula.
 * Covers C05 (jump-crit + system crit never stack) and "replace, not add" (old weapon damage gone).
 */
public class MeleePipelineTest {

    private static final double EPS = 1e-9;
    private final EmberTables t = EmberTables.defaults();
    private static final double B = 58.6;

    private double p1(double raw, double attr, double ench, boolean jumpState, boolean critRoll) {
        ChargeEstimator.Result r = ChargeEstimator.estimate(raw, attr, ench, jumpState, false);
        boolean crit = !r.unreliable && critRoll && EmberFormula.critEligible(t, r.charge);
        return EmberFormula.melee(t, B, r.charge, crit);
    }

    @Test public void fullSwingIsExactlyB() {
        double raw = ChargeEstimator.vanillaMelee(7.0, 0, 1.0, false); // diamond sword
        assertEquals(B, p1(raw, 7.0, 0, false, false), 1e-6);
    }

    @Test public void vanillaWeaponAndSharpnessDoNotLeakIn() {
        // T3 diamond sword with Sharpness V vs bare iron sword: same P1 result at full charge
        double a = p1(ChargeEstimator.vanillaMelee(7.0, ChargeEstimator.sharpnessBonus(5), 1.0, false), 7.0, ChargeEstimator.sharpnessBonus(5), false, false);
        double b = p1(ChargeEstimator.vanillaMelee(6.0, 0, 1.0, false), 6.0, 0, false, false);
        assertEquals(a, b, 1e-6);
        assertEquals(B, a, 1e-6);
    }

    @Test public void jumpCritIsStrippedAndSystemCritAppliesOnce() {
        double raw = ChargeEstimator.vanillaMelee(7.0, 0, 1.0, true); // vanilla ×1.5 already inside raw
        assertEquals(B, p1(raw, 7.0, 0, true, false), 1e-6);          // no system crit → plain B
        assertEquals(B * 1.5, p1(raw, 7.0, 0, true, true), 1e-6);     // system crit → ×1.5 once, not ×2.25
    }

    @Test public void lowChargeNeverCrits() {
        double raw = ChargeEstimator.vanillaMelee(7.0, 0, 0.5, false);
        assertEquals(B * (0.2 + 0.8 * 0.25), p1(raw, 7.0, 0, false, true), 1e-6);
    }

    @Test public void playerDefenseAppliedOnce() {
        // T2 charm: an enemy hit of 20 after the P1 pipeline is 20 × 0.8, independent of vanilla armor
        EmberLoadout l = EmberLoadout.compute(t, null, EmberItemData.create("burst", "charm", 2, 0, 0, 6, true, "admin"), 30);
        assertEquals(16.0, 20.0 * l.m, EPS);
        assertEquals(0.5, EmberFormula.mitigation(t, 1000), EPS); // the floor cannot be undercut by stacking
    }
}
