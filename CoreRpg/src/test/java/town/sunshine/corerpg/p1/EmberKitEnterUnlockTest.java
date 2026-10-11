package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2294: Q0x first-enter kit unlocks. */
public class EmberKitEnterUnlockTest {

    @Test
    public void dashParryStepEnterOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markDashEnter(d));
        assertFalse(EmberSkillKit.markDashEnter(d));
        assertTrue(EmberSkillKit.dashUnlocked(d, null));

        assertTrue(EmberSkillKit.markParryEnter(d));
        assertTrue(EmberSkillKit.parryUnlocked(d, null));

        assertTrue(EmberSkillKit.markStepEnter(d));
        assertTrue(EmberSkillKit.stepVariantUnlocked(d, null));

        assertTrue(EmberSkillKit.markAttuneEnter(d));
        assertTrue(EmberSkillKit.attuneUnlocked(d, null));
    }

    @Test
    public void stepLandSyncBump() {
        assertEquals(30, EmberSkillKit.stepLandSlowTicks(false));
        assertEquals(34, EmberSkillKit.stepLandSlowTicks(true));
        assertEquals(0.005, EmberSkillKit.stepLandHealPct(false), 1e-9);
        assertEquals(0.00625, EmberSkillKit.stepLandHealPct(true), 1e-9);
    }

    @Test
    public void dashAndShieldSyncBump() {
        assertEquals(30, EmberSkillKit.dashLandSlowTicks(false));
        assertEquals(34, EmberSkillKit.dashLandSlowTicks(true));
        assertEquals(0.005, EmberSkillKit.dashLandHealPct(false), 1e-9);
        assertEquals(0.00625, EmberSkillKit.dashLandHealPct(true), 1e-9);
        assertEquals(5.0, EmberSkillKit.sigShieldSecs(5.0, false), 1e-9);
        assertEquals(6.25, EmberSkillKit.sigShieldSecs(5.0, true), 1e-9);
    }

    @Test
    public void parrySyncConstants() {
        assertEquals(24, EmberSkillKit.PARRY_SYNC_SLOW_TICKS);
        assertEquals(0.00625, EmberSkillKit.PARRY_SYNC_HEAL_PCT, 1e-9);
    }

    @Test
    public void pledgeEnterOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markPledgeEnter(d));
        assertFalse(EmberSkillKit.markPledgeEnter(d));
        assertTrue(EmberSkillKit.pledgeUnlocked(d, null));
    }

    @Test
    public void dashRadiusAndParryWindowSync() {
        assertEquals(1.25, EmberSkillKit.dashHitRadius(false), 1e-9);
        assertEquals(1.45, EmberSkillKit.dashHitRadius(true), 1e-9);
        assertEquals(450L, EmberSkillKit.parryWindowMs(false));
        assertEquals(500L, EmberSkillKit.parryWindowMs(true));
    }

    @Test
    public void sigChargeAndShieldMaxSync() {
        assertEquals(0.5, EmberSkillKit.sigChargeSecs(0.5, false), 1e-9);
        assertEquals(0.4, EmberSkillKit.sigChargeSecs(0.5, true), 1e-9);
        assertEquals(1.0, EmberSkillKit.sigShieldMaxMult(false), 1e-9);
        assertEquals(1.25, EmberSkillKit.sigShieldMaxMult(true), 1e-9);
    }

    @Test
    public void strideEnterAndSigFeelSync() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markStrideEnter(d));
        assertFalse(EmberSkillKit.markStrideEnter(d));
        assertTrue(EmberSkillKit.strideMapUnlocked(d, null));
        assertEquals(15.0 * 0.85, EmberSkillKit.sigDodgeIcdSecs(15.0, true), 1e-9);
        assertEquals(10.0 * 0.85, EmberSkillKit.sigSpreadIcdSecs(10.0, true), 1e-9);
        assertEquals(4.0, EmberSkillKit.sigBurnSpreadRadius(false), 1e-9);
        assertEquals(4.5, EmberSkillKit.sigBurnSpreadRadius(true), 1e-9);
        assertEquals(2, EmberSkillKit.sigIgniteN(1, true));
        assertEquals(0.45, EmberSkillKit.sigSustainLow(0.4, true), 1e-9);
    }

    @Test
    public void potionAndSustainEverySync() {
        assertEquals(1.05 * 1.05, EmberSkillKit.sigPotionMult(1.05, true), 1e-9);
        assertEquals(1.0, EmberSkillKit.sigPotionMult(1.0, true), 1e-9);
        assertEquals(-2, EmberSkillKit.sigSustainEvery(-1, true));
        assertEquals(0, EmberSkillKit.sigSustainEvery(0, true));
    }

    @Test
    public void burnTicksAndBurstPrimeSync() {
        assertEquals(2, EmberSkillKit.sigBurnTicksExtra(1, true));
        assertEquals(0, EmberSkillKit.sigBurnTicksExtra(0, true));
        assertEquals(2, EmberSkillKit.sigBurstPrime(1, true));
        assertEquals(0, EmberSkillKit.sigBurstPrime(0, true));
    }
}
