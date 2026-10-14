package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/** TIP-DB D13333: abyss_settle_* remember + line helpers. */
public class EmberAbyssSettleTest {

    @Test
    public void rememberAndLastFloor_overwrite() {
        PlayerData d = new PlayerData();
        assertEquals(0, EmberAbyssSettle.lastFloor(d));
        assertTrue(EmberAbyssSettle.remember(d, 3));
        assertEquals(3, EmberAbyssSettle.lastFloor(d));
        assertFalse(EmberAbyssSettle.remember(d, 3));
        assertTrue(EmberAbyssSettle.remember(d, 7));
        assertEquals(7, EmberAbyssSettle.lastFloor(d));
        assertFalse(EmberAbyssSettle.remember(d, 0));
        assertFalse(EmberAbyssSettle.remember(d, 99));
        assertEquals(7, EmberAbyssSettle.lastFloor(d));
    }

    @Test
    public void displayNameAndLine() {
        assertEquals("第 3 层", EmberAbyssSettle.displayName(3));
        assertEquals("", EmberAbyssSettle.displayName(0));
        assertEquals("第 3 层 · 冲层",
                EmberAbyssSettle.line(3, EmberAbyssPath.PUSH));
        assertEquals("第 3 层 · 刷层",
                EmberAbyssSettle.line(3, EmberAbyssPath.FARM));
        assertEquals("第 3 层", EmberAbyssSettle.line(3, EmberAbyssPath.NONE));
        assertEquals("", EmberAbyssSettle.line(0, EmberAbyssPath.PUSH));
    }

    @Test
    public void settleNextLoopConstants_menuOpen() {
        assertEquals("/ember_p1_abyss_settle", EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD);
        assertEquals("[下一环]", EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL);
        assertEquals("/ember_p1_adventure", EmberAbyssSettle.SETTLE_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberAbyssSettle.SETTLE_FORGE_CMD);
        assertEquals("/ember_p1_abyss", EmberAbyssSettle.SETTLE_ABYSS_CMD);
        assertFalse(EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD.contains("corerpg p1"));
        assertFalse(EmberAbyssSettle.SETTLE_FORGE_CMD.contains("corerpg p1 forge"));
        assertFalse(EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD.contains("abysstarget"));
    }
}
