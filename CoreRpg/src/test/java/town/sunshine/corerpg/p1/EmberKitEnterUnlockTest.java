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
}
