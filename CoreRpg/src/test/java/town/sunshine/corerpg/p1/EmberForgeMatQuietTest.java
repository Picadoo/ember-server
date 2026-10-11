package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberForgeMatQuietTest {
    @Test
    public void risingEdgeShared() {
        assertTrue(EmberForgeMatReady.risingEdge(0, 3, true));
        assertFalse(EmberForgeMatReady.risingEdge(3, 3, true));
        assertFalse(EmberForgeMatReady.risingEdge(0, 3, false));
    }

    @Test
    public void quietLatchIndependent() {
        PlayerData d = new PlayerData();
        EmberForgeMatReady.setQuietLatch(d, 4);
        assertEquals(4, d.periodCount(EmberForgeMatReady.C_QUIET, "all"));
        EmberForgeMatReady.clearQuietLatch(d);
        assertEquals(0, d.periodCount(EmberForgeMatReady.C_QUIET, "all"));
    }
}
