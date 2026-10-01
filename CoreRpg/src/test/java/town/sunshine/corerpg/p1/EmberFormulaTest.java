package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/** 策划书 §7.2/§7.4 reference values and edge cases. */
public class EmberFormulaTest {

    private static final double EPS = 1e-9;
    private final EmberTables t = EmberTables.defaults();

    // ---- §7.4 例 1: T2, Lv30, 双 +6, 标准成色, 零精工 ----

    @Test public void example1_baseAttack() {
        assertEquals(58.6, EmberFormula.baseAttack(t, 2, 0, 0, 6, 30), EPS);
    }

    @Test public void example1_hpDefenseEhp() {
        double h0 = EmberFormula.baseHp(t, 2, 0, 0, 6, 30);
        assertEquals(163.5, h0, EPS);
        double d = EmberFormula.defense(t, 2);
        assertEquals(10.0, d, EPS);
        double m = EmberFormula.mitigation(t, d);
        assertEquals(0.8, m, EPS);
        assertEquals(204.375, EmberFormula.ehp(EmberFormula.maxHp(t, h0, false), m), EPS);
    }

    @Test public void example1_sustain() {
        double h = EmberFormula.maxHp(t, EmberFormula.baseHp(t, 2, 0, 0, 6, 30), true);
        assertEquals(183.12, h, EPS);
        assertEquals(228.9, EmberFormula.ehp(h, 0.8), EPS);
    }

    @Test public void example1_expectedDps() {
        // 1.6 swings/s with 10%×1.5 crit, plus one 烬斩 (1.5B) every 8 s → ≈109.44/s
        double b = EmberFormula.baseAttack(t, 2, 0, 0, 6, 30);
        double dps = 1.6 * EmberFormula.expectedFullSwing(t, b) + EmberFormula.skill(t, b) / 8.0;
        assertEquals(109.44, dps, 0.005);
    }

    // ---- §7.4 例 2: T3, Lv60, 双 +10, 极品, 满精工 ----

    @Test public void example2_top() {
        double b = EmberFormula.baseAttack(t, 3, 3, 3, 10, 60);
        assertEquals(134.6, b, EPS);
        double h0 = EmberFormula.baseHp(t, 3, 3, 3, 10, 60);
        assertEquals(363.7, h0, EPS);
        double m = EmberFormula.mitigation(t, EmberFormula.defense(t, 3));
        assertEquals(40.0 / 54.0, m, EPS);
        assertEquals(490.995, EmberFormula.ehp(h0, m), 1e-6);
        double hs = EmberFormula.maxHp(t, h0, true);
        assertEquals(407.344, hs, 1e-9);
        assertEquals(549.9144, EmberFormula.ehp(hs, m), 1e-6);
    }

    @Test public void growthIsAdditiveNotMultiplicative() {
        // §7.3: +60% 强化 + 12% 成色 + 6% 精工 = ×1.78, not 1.60×1.12×1.06
        assertEquals(1.78, EmberFormula.growth(t, 3, 3, 10), EPS);
        assertNotEquals(1.60 * 1.12 * 1.06, EmberFormula.growth(t, 3, 3, 10), 1e-3);
        // max add from quality+craft is 18%
        assertEquals(1.18, EmberFormula.growth(t, 3, 3, 0), EPS);
    }

    // ---- level part ----

    @Test public void levelPartFromLv10CappedAtLv60() {
        assertEquals(0.0, EmberFormula.levelAttack(t, 1), EPS);
        assertEquals(0.0, EmberFormula.levelAttack(t, 10), EPS);
        assertEquals(0.2, EmberFormula.levelAttack(t, 11), EPS);
        assertEquals(10.0, EmberFormula.levelAttack(t, 60), EPS);
        assertEquals(10.0, EmberFormula.levelAttack(t, 99), EPS);
        assertEquals(50.0, EmberFormula.levelHp(t, 60), EPS);
        assertEquals(50.0, EmberFormula.levelHp(t, 75), EPS);
        assertEquals(0.0, EmberFormula.levelHp(t, 0), EPS);
    }

    @Test public void noWeaponNoCharm() {
        assertEquals(1.0, EmberFormula.baseAttack(t, -1, 0, 0, 0, 10), EPS);
        assertEquals(10.0, EmberFormula.baseAttack(t, -1, 0, 0, 0, 60), EPS);
        assertEquals(20.0, EmberFormula.baseHp(t, -1, 0, 0, 0, 10), EPS);
        assertEquals(0.0, EmberFormula.defense(t, -1), EPS);
        assertEquals(1.0, EmberFormula.mitigation(t, 0), EPS);
    }

    @Test public void tierTableT0toT3() {
        assertEquals(12.0, EmberFormula.baseAttack(t, 0, 0, 0, 0, 10), EPS);
        assertEquals(24.0, EmberFormula.baseAttack(t, 1, 0, 0, 0, 10), EPS);
        assertEquals(40.0, EmberFormula.baseHp(t, 0, 0, 0, 0, 10), EPS);
        assertEquals(185.0, EmberFormula.baseHp(t, 3, 0, 0, 0, 10), EPS);
        assertEquals(6.0, EmberFormula.defense(t, 1), EPS);
    }

    // ---- defense floor ----

    @Test public void defenseFloorIsHalf() {
        assertEquals(0.5, EmberFormula.mitigation(t, 40), EPS);
        assertEquals(0.5, EmberFormula.mitigation(t, 100), EPS);
        assertEquals(0.5, EmberFormula.mitigation(t, 1e9), EPS);
        assertEquals(40.0 / 79.0, EmberFormula.mitigation(t, 39), EPS);
        assertTrue(EmberFormula.mitigation(t, 39) > 0.5);
    }

    @Test public void defenseBadInputMeansNoDefense() {
        assertEquals(1.0, EmberFormula.mitigation(t, Double.NaN), EPS);
        assertEquals(1.0, EmberFormula.mitigation(t, -5), EPS);
        assertEquals(1.0, EmberFormula.mitigation(t, Double.POSITIVE_INFINITY), EPS);
    }

    @Test public void maxTierDefenseNeverHitsFloor() {
        // T3 charm D=14 → M = 40/54 ≈ 0.7407 > 0.5: the floor is a guard, not a reachable P1 state
        assertTrue(EmberFormula.mitigation(t, EmberFormula.defense(t, 3)) > t.defFloor);
    }

    // ---- crit ----

    @Test public void critFixedTenPercentOnePointFive() {
        assertEquals(0.10, t.critRate, EPS);
        assertEquals(1.5, t.critMult, EPS);
        assertTrue(EmberFormula.rollCrit(t, 1.0, 0.0));
        assertTrue(EmberFormula.rollCrit(t, 1.0, 0.0999));
        assertFalse(EmberFormula.rollCrit(t, 1.0, 0.10));
        assertFalse(EmberFormula.rollCrit(t, 1.0, 0.5));
        assertEquals(58.6 * 1.5, EmberFormula.melee(t, 58.6, 1.0, true), EPS);
        assertEquals(58.6, EmberFormula.melee(t, 58.6, 1.0, false), EPS);
    }

    @Test public void critNeedsChargeAtLeast09() {
        assertTrue(EmberFormula.critEligible(t, 0.9));
        assertFalse(EmberFormula.critEligible(t, 0.8999));
        assertFalse(EmberFormula.rollCrit(t, 0.89, 0.0));
        // a crit flag on a low-charge swing is ignored by melee()
        double c = 0.5;
        assertEquals(58.6 * (0.2 + 0.8 * 0.25), EmberFormula.melee(t, 58.6, c, true), EPS);
    }

    @Test public void critOnlyOnce() {
        // C05: vanilla jump-crit is replaced, the result is exactly B×1.5, never B×1.5×1.5
        double v = EmberFormula.melee(t, 100, 1.0, true);
        assertEquals(150.0, v, EPS);
    }

    @Test public void chargeFactorCurve() {
        assertEquals(0.2, EmberFormula.chargeFactor(0), EPS);
        assertEquals(1.0, EmberFormula.chargeFactor(1), EPS);
        assertEquals(0.2 + 0.8 * 0.81, EmberFormula.chargeFactor(0.9), EPS);
        assertEquals(1.0, EmberFormula.chargeFactor(5), EPS);
        assertEquals(0.2, EmberFormula.chargeFactor(Double.NaN), EPS);
        assertEquals(0.2, EmberFormula.chargeFactor(-1), EPS);
    }

    @Test public void meleeNeverNegativeOrNaN() {
        assertEquals(0.0, EmberFormula.melee(t, Double.NaN, 1, true), EPS);
        assertEquals(0.0, EmberFormula.melee(t, -3, 1, false), EPS);
        assertEquals(0.0, EmberFormula.skill(t, Double.NaN), EPS);
    }

    @Test public void skillIsOnePointFiveB() {
        assertEquals(87.9, EmberFormula.skill(t, 58.6), 1e-9);
    }

    // ---- monotonicity (§8 boundary argument) ----

    @Test public void monotonicInEveryInput() {
        for (int tier = 0; tier <= 3; tier++)
            for (int q = 0; q <= 3; q++)
                for (int f = 0; f <= 3; f++)
                    for (int e = 0; e <= 10; e++) {
                        double b = EmberFormula.baseAttack(t, tier, q, f, e, 30);
                        double h = EmberFormula.baseHp(t, tier, q, f, e, 30);
                        if (e < 10) assertTrue(EmberFormula.baseAttack(t, tier, q, f, e + 1, 30) > b);
                        if (q < 3) assertTrue(EmberFormula.baseHp(t, tier, q + 1, f, e, 30) > h);
                        if (tier < 3) assertTrue(EmberFormula.baseAttack(t, tier + 1, q, f, e, 30) > b);
                    }
    }

    // ---- config validation ----

    @Test public void tablesOverrideValidated() {
        EmberTables x = EmberTables.of(Arrays.asList(10, 20, 30, 40), null, null, null, null, null,
                0.1, 1.5, 40, 0.5, 1.12);
        assertEquals(30.0, x.weaponA(2), EPS);
        assertEquals(95.0, x.charmH(2), EPS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tablesRejectWrongLength() {
        EmberTables.of(Arrays.asList(1, 2, 3), null, null, null, null, null, 0.1, 1.5, 40, 0.5, 1.12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tablesRejectNonMonotonic() {
        EmberTables.of(null, null, null, null, null, Arrays.asList(0, 4, 8, 13, 18, 24, 30, 37, 44, 52, 50),
                0.1, 1.5, 40, 0.5, 1.12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tablesRejectBadFloor() {
        EmberTables.of(null, null, null, null, null, null, 0.1, 1.5, 40, 0.0, 1.12);
    }
}
