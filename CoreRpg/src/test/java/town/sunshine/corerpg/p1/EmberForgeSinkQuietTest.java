package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberForgeSinkQuietTest {
    @Test
    public void risingEdge() {
        assertTrue(EmberForgeSinkQuiet.risingEdge(0, 1, true));
        assertFalse(EmberForgeSinkQuiet.risingEdge(1, 1, true));
        assertFalse(EmberForgeSinkQuiet.risingEdge(0, 1, false));
    }

    @Test
    public void codexQuietBitmask() {
        PlayerData d = new PlayerData();
        assertEquals(-1, EmberCodex.nextQuietClaimable(d));
        // fake claimable by registering entries and not claiming - heavy; just bitmask
        EmberCodex.markQuietStage(d, 0);
        assertTrue(EmberCodex.stageQuietTold(d, 0));
        assertFalse(EmberCodex.stageQuietTold(d, 1));
        EmberCodex.markQuietStage(d, 1);
        assertTrue(EmberCodex.stageQuietTold(d, 1));
        assertEquals(3, EmberCodex.quietMask(d));
    }
}
