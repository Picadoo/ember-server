
package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.*;

import org.junit.Test;

/** D2474–D2475 posture footwork. */
public class EmberPostureFootworkTest {
    @Test
    public void stepLandPostureBump() {
        assertEquals(30, EmberSkillKit.stepLandSlowTicks(false, false));
        assertEquals(36, EmberSkillKit.stepLandSlowTicks(true, true));
        assertEquals(0.005 * 1.25 * 1.1, EmberSkillKit.stepLandHealPct(true, true), 1e-9);
    }

    @Test
    public void stepTravelByPosture() {
        assertEquals(4.0, EmberSkillKit.stepTravelDistance(5.0, true, false, EmberPosturePath.GUARD), 1e-9);
        assertEquals(4.5, EmberSkillKit.stepTravelDistance(5.0, true, true, EmberPosturePath.GUARD), 1e-9);
        assertEquals(5.0, EmberSkillKit.stepTravelDistance(5.0, false, false, EmberPosturePath.STRIKE), 1e-9);
        assertEquals(5.5, EmberSkillKit.stepTravelDistance(5.0, false, true, EmberPosturePath.STRIKE), 1e-9);
        assertEquals(5.5, EmberSkillKit.stepTravelDistance(5.0, false, true, EmberPosturePath.SWEEP), 1e-9);
        assertEquals(5.0, EmberSkillKit.stepTravelDistance(5.0, false, true, EmberPosturePath.GUARD), 1e-9);
    }

    @Test
    public void guardParryWindow() {
        assertEquals(450L, EmberSkillKit.parryWindowMs(false, false));
        assertEquals(500L, EmberSkillKit.parryWindowMs(true, false));
        assertEquals(550L, EmberSkillKit.parryWindowMs(true, true));
        assertEquals(500L, EmberSkillKit.parryWindowMs(false, true));
    }

    @Test
    public void roomEnterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markRoomEnter(d));
        assertFalse(EmberSkillKit.markRoomEnter(d));
        assertTrue(EmberSkillKit.roomPathUnlocked(d, null));
    }
}
