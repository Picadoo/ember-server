package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberAbyssPathTest {
    @Test public void parse_enter_tier() {
        assertEquals(EmberAbyssPath.PUSH, EmberAbyssPath.parse("冲层"));
        assertEquals(EmberAbyssPath.FARM, EmberAbyssPath.parse("farm"));
        assertEquals(EmberAbyssPath.NONE, EmberAbyssPath.parse("clear"));
        assertEquals(4, EmberAbyssPath.enterTier(EmberAbyssPath.PUSH, 3, 4));
        assertEquals(3, EmberAbyssPath.enterTier(EmberAbyssPath.FARM, 3, 4));
        assertEquals(1, EmberAbyssPath.enterTier(EmberAbyssPath.FARM, 0, 1));
        assertTrue(EmberAbyssPath.enterCmd(EmberAbyssPath.PUSH, 0, 1).contains("next"));
        assertTrue(EmberAbyssPath.tip(EmberAbyssPath.FARM).contains("成色"));
    }
}
