package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** D2769+ Chest/Hunt (no Speed/Resist land twin). */
public class EmberChestHuntBehaviorTest {

    @Test
    public void chestHuntEnterLatches() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSkillKit.markChestEnter(d));
        assertFalse(EmberSkillKit.markChestEnter(d));
        assertTrue(EmberSkillKit.chestPathUnlocked(d, null));
        PlayerData d2 = new PlayerData();
        assertTrue(EmberSkillKit.markHuntEnter(d2));
        assertTrue(EmberSkillKit.huntPathUnlocked(d2, null));
    }

    @Test
    public void chestAutoFasterAskCd() {
        assertEquals(4, EmberChestPath.autoDelayTicks(EmberChestPath.AUTO));
        assertEquals(10, EmberChestPath.autoDelayTicks(EmberChestPath.ASK));
        assertEquals(5000L, EmberChestPath.askCooldownMs(EmberChestPath.ASK));
        assertEquals(8000L, EmberChestPath.askCooldownMs(EmberChestPath.NONE));
    }

    @Test
    public void huntAutoFasterAskCd() {
        assertEquals(3, EmberHuntPath.autoDelayTicks(EmberHuntPath.AUTO));
        assertEquals(8, EmberHuntPath.autoDelayTicks(EmberHuntPath.ASK));
        assertEquals(5000L, EmberHuntPath.askCooldownMs(EmberHuntPath.ASK));
        assertEquals(8000L, EmberHuntPath.askCooldownMs(EmberHuntPath.NONE));
    }
}
