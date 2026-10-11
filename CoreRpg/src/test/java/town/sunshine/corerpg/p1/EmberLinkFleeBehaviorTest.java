package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2721+ Link/Flee behavior (no Speed/Resist land twin). */
public class EmberLinkFleeBehaviorTest {

    @Test
    public void linkFleeEnterLatches() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markLinkEnter(d));
        assertFalse(EmberSkillKit.markLinkEnter(d));
        assertTrue(EmberSkillKit.linkPathUnlocked(d, null));
        PlayerData d2 = new PlayerData();
        assertTrue(EmberSkillKit.markFleeEnter(d2));
        assertTrue(EmberSkillKit.fleePathUnlocked(d2, null));
    }

    @Test
    public void linkAutoFasterAskCd() {
        assertEquals(2, EmberLinkPath.autoDelayTicks(EmberLinkPath.AUTO));
        assertEquals(5, EmberLinkPath.autoDelayTicks(EmberLinkPath.ASK));
        assertEquals(5000L, EmberLinkPath.askCooldownMs(EmberLinkPath.ASK));
        assertEquals(8000L, EmberLinkPath.askCooldownMs(EmberLinkPath.NONE));
    }

    @Test
    public void fleeAutoFaster() {
        assertEquals(8, EmberFleePath.autoDelayTicks(EmberFleePath.AUTO));
        assertEquals(15, EmberFleePath.autoDelayTicks(EmberFleePath.ASK));
    }
}
