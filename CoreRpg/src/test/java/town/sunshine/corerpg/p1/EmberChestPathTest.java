package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberChestPathTest {
    @Test public void policies() {
        assertTrue(EmberChestPath.shouldAuto(EmberChestPath.AUTO));
        assertTrue(EmberChestPath.shouldMute(EmberChestPath.MUTE));
        assertEquals(EmberChestPath.ASK, EmberChestPath.parse("提醒"));
        assertEquals(EmberRunRules.Extra.CHEST, EmberExtraPath.toExtra(EmberExtraPath.CHEST));
    }
}
