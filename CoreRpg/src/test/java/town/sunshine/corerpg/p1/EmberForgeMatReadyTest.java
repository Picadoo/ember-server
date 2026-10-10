package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberForgeMatReadyTest {
    @Test public void rising_edge_and_lines() {
        assertTrue(EmberForgeMatReady.risingEdge(0, 3, true));
        assertFalse(EmberForgeMatReady.risingEdge(3, 3, true));
        assertTrue(EmberForgeMatReady.risingEdge(3, 4, true));
        assertFalse(EmberForgeMatReady.risingEdge(0, 3, false));
        assertTrue(EmberForgeMatReady.actionBar(2, 3).contains("材料就绪"));
        assertTrue(EmberForgeMatReady.chatLine("刃", 2, 3).contains("强化材料够了"));
    }
}
