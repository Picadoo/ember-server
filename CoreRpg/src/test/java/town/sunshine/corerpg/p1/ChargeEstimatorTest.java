package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/** F03 damage-ratio charge estimation, including the ≥0.9 rule boundary. */
public class ChargeEstimatorTest {

    private static final double TOL = 1e-6;

    @Test public void roundTripPlainSwing() {
        double[] attrs = {1.0, 4.0, 7.0, 8.0};
        double[] ench = {0.0, ChargeEstimator.sharpnessBonus(1), ChargeEstimator.sharpnessBonus(5)};
        for (double a : attrs)
            for (double e : ench)
                for (int i = 0; i <= 100; i++) {
                    double c = i / 100.0;
                    double raw = ChargeEstimator.vanillaMelee(a, e, c, false);
                    ChargeEstimator.Result r = ChargeEstimator.estimate(raw, a, e, false, false);
                    assertEquals("a=" + a + " e=" + e + " c=" + c, c, r.charge, TOL);
                    assertFalse(r.vanillaCrit);
                    assertFalse(r.unreliable);
                }
    }

    @Test public void roundTripJumpCrit() {
        for (int i = 91; i <= 100; i++) {
            double c = i / 100.0;
            double raw = ChargeEstimator.vanillaMelee(7.0, 3.0, c, true);
            ChargeEstimator.Result r = ChargeEstimator.estimate(raw, 7.0, 3.0, true, false);
            assertEquals(c, r.charge, TOL);
            assertTrue(r.vanillaCrit);
        }
    }

    @Test public void airborneButLowChargeIsNotACrit() {
        // jump state possible, but vanilla only crits above 0.9 → estimator falls back to k=1
        double raw = ChargeEstimator.vanillaMelee(7.0, 0.0, 0.85, false);
        ChargeEstimator.Result r = ChargeEstimator.estimate(raw, 7.0, 0.0, true, false);
        assertEquals(0.85, r.charge, TOL);
        assertFalse(r.vanillaCrit);
    }

    @Test public void thresholdNinetyPercent() {
        EmberTables t = EmberTables.defaults();
        double raw09 = ChargeEstimator.vanillaMelee(7.0, 0.0, 0.9, false);
        assertTrue(EmberFormula.critEligible(t, ChargeEstimator.estimate(raw09, 7.0, 0, false, false).charge));
        double raw089 = ChargeEstimator.vanillaMelee(7.0, 0.0, 0.89, false);
        assertFalse(EmberFormula.critEligible(t, ChargeEstimator.estimate(raw089, 7.0, 0, false, false).charge));
    }

    @Test public void floatRoundingAtFullCharge() {
        // vanilla computes in float; a full swing can land slightly above A
        float raw = (float) (7.0f * (0.2f + 1.0f * 1.0f * 0.8f));
        ChargeEstimator.Result r = ChargeEstimator.estimate(raw + 1e-5, 7.0, 0, false, false);
        assertEquals(1.0, r.charge, 1e-4);
        assertFalse(r.unreliable);
    }

    @Test public void zeroChargeFloor() {
        ChargeEstimator.Result r = ChargeEstimator.estimate(0.2 * 7.0, 7.0, 0, false, false);
        assertEquals(0.0, r.charge, TOL);
        r = ChargeEstimator.estimate(0.1, 7.0, 0, false, false);
        assertEquals(0.0, r.charge, TOL);
    }

    @Test public void rawAboveFullSwingIsUnreliable() {
        ChargeEstimator.Result r = ChargeEstimator.estimate(20.0, 7.0, 0, false, false);
        assertTrue(r.unreliable);
        assertEquals(1.0, r.charge, TOL);
    }

    @Test public void badInputIsUnreliableZero() {
        assertTrue(ChargeEstimator.estimate(Double.NaN, 7, 0, false, false).unreliable);
        assertTrue(ChargeEstimator.estimate(5, 0, 0, false, false).unreliable);
        assertTrue(ChargeEstimator.estimate(5, Double.NaN, 0, false, false).unreliable);
        assertEquals(0.0, ChargeEstimator.estimate(-1, 7, 0, false, false).charge, TOL);
    }

    @Test public void smiteBaneFlagged() {
        double raw = ChargeEstimator.vanillaMelee(7.0, 0.0, 1.0, false);
        assertTrue(ChargeEstimator.estimate(raw, 7.0, 0, false, true).unreliable);
    }

    @Test public void sharpnessBonusTable() {
        assertEquals(0.0, ChargeEstimator.sharpnessBonus(0), TOL);
        assertEquals(1.0, ChargeEstimator.sharpnessBonus(1), TOL);
        assertEquals(3.0, ChargeEstimator.sharpnessBonus(5), TOL);
    }
}
