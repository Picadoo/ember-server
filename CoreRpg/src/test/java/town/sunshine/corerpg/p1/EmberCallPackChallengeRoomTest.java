package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2673+ Call/Pack + Challenge×Room helpers. */
public class EmberCallPackChallengeRoomTest {

    @Test
    public void callPackEnterLatches() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markCallEnter(d));
        assertFalse(EmberSkillKit.markCallEnter(d));
        assertTrue(EmberSkillKit.callPathUnlocked(d, null));
        PlayerData d2 = new PlayerData();
        assertTrue(EmberSkillKit.markPackEnter(d2));
        assertTrue(EmberSkillKit.packPathUnlocked(d2, null));
    }

    @Test
    public void callAskCooldown() {
        assertEquals(5000L, EmberCallPath.askCooldownMs(EmberCallPath.ASK));
        assertEquals(8000L, EmberCallPath.askCooldownMs(EmberCallPath.NONE));
    }

    @Test
    public void packAutoTightEarlier() {
        assertEquals(3, EmberPackPath.tightSlotsFor(EmberPackPath.AUTO));
        assertEquals(1, EmberPackPath.tightSlotsFor(EmberPackPath.ASK));
        assertEquals(1, EmberPackPath.tightSlotsFor(EmberPackPath.NONE));
    }

    @Test
    public void packAskCooldown() {
        assertEquals(12_000L, EmberPackPath.askCooldownMs(EmberPackPath.ASK));
        assertEquals(20_000L, EmberPackPath.askCooldownMs(EmberPackPath.NONE));
    }
}
