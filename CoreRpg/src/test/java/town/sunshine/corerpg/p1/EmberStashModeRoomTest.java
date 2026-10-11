package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2817+ Stash / Mode×Room. */
public class EmberStashModeRoomTest {

    @Test
    public void stashEnterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markStashEnter(d));
        assertFalse(EmberSkillKit.markStashEnter(d));
        assertTrue(EmberSkillKit.stashPathUnlocked(d, null));
    }

    @Test
    public void stashAskCooldown() {
        assertEquals(5000L, EmberStashPath.askCooldownMs(EmberStashPath.ASK));
        assertEquals(8000L, EmberStashPath.askCooldownMs(EmberStashPath.NONE));
    }

    @Test
    public void modeHardAbyssOrEcho() {
        EmberRunSession s = new EmberRunSession();
        s.abyss = 1;
        assertTrue(EmberRoomPath.modeHardSession(s, null, null));
        EmberRunSession s2 = new EmberRunSession();
        s2.mapKey = "echo_q01";
        assertTrue(EmberRoomPath.modeHardSession(s2, null, null));
        EmberRunSession s3 = new EmberRunSession();
        s3.mapKey = "q01";
        assertFalse(EmberRoomPath.modeHardSession(s3, null, null));
    }

    @Test
    public void modeHardReversePledge() {
        PlayerData d = new PlayerData();
        EmberPledgeService.applyToggle(d, "reverse");
        EmberRunSession s = new EmberRunSession();
        s.mapKey = "q02";
        assertTrue(EmberRoomPath.modeHardSession(s, d, null));
    }

    @Test
    public void outpostHard() {
        EmberRunSession s = new EmberRunSession();
        s.mapKey = "outpost";
        assertTrue(EmberRoomPath.modeHardSession(s, null, null));
    }
}
