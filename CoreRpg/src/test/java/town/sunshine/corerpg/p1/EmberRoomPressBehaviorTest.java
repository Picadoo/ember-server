package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2514–D2516 room/press behavior. */
public class EmberRoomPressBehaviorTest {

    @Test
    public void applyBiasPinsBothToPreferred() {
        String[] rolled = new String[]{"r3", "burn", "r2", "timed"};
        String[] out = EmberRoomPath.applyBias(rolled, EmberRoomPath.FRONT);
        assertEquals("r1", out[0]);
        assertEquals("burn", out[1]);
        assertEquals("r1", out[2]);
        assertEquals("timed", out[3]);
    }

    @Test
    public void applyBiasEventKindOnly() {
        String[] rolled = new String[]{"", "", "", "ambush"};
        String[] out = EmberRoomPath.applyBias(rolled, EmberRoomPath.BACK);
        assertEquals("", out[0]);
        assertEquals("", out[1]);
        assertEquals("r3", out[2]);
        assertEquals("ambush", out[3]);
    }

    @Test
    public void preferredRoomMatch() {
        assertTrue(EmberRoomPath.isPreferredRoom(EmberRoomPath.MID, "r2"));
        assertFalse(EmberRoomPath.isPreferredRoom(EmberRoomPath.MID, "r1"));
    }

    @Test
    public void pressEnterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markPressEnter(d));
        assertFalse(EmberSkillKit.markPressEnter(d));
        assertTrue(EmberSkillKit.pressPathUnlocked(d, null));
    }

    @Test
    public void askCooldownFasterOnAskPath() {
        assertEquals(5000L, EmberPressPath.askCooldownMs(EmberPressPath.ASK));
        assertEquals(8000L, EmberPressPath.askCooldownMs(EmberPressPath.NONE));
        assertEquals(8000L, EmberPressPath.askCooldownMs(EmberPressPath.AUTO));
    }

    @Test
    public void frontOpenLongerBackClearLonger() {
        assertEquals(80, EmberRoomPath.openSpeedTicks(EmberRoomPath.FRONT));
        assertEquals(40, EmberRoomPath.openSpeedTicks(EmberRoomPath.BACK));
        assertEquals(30, EmberRoomPath.clearSpeedTicks(EmberRoomPath.FRONT));
        assertEquals(60, EmberRoomPath.clearSpeedTicks(EmberRoomPath.BACK));
    }

    @Test
    public void autoPressSlowLonger() {
        assertEquals(30, EmberPressPath.pressBossSlowTicks(EmberPressPath.AUTO));
        assertEquals(20, EmberPressPath.pressBossSlowTicks(EmberPressPath.ASK));
    }

    @Test
    public void cutEnterLatch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markCutEnter(d));
        assertFalse(EmberSkillKit.markCutEnter(d));
        assertTrue(EmberSkillKit.cutPathUnlocked(d, null));
    }

    @Test
    public void cutAskCooldownAndAutoSlow() {
        assertEquals(5000L, EmberCutPath.askCooldownMs(EmberCutPath.ASK));
        assertEquals(8000L, EmberCutPath.askCooldownMs(EmberCutPath.NONE));
        assertEquals(30, EmberCutPath.cutRegenSlowTicks(EmberCutPath.AUTO));
        assertEquals(20, EmberCutPath.cutRegenSlowTicks(EmberCutPath.ASK));
    }
}
