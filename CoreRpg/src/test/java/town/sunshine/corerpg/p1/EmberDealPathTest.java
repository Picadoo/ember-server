package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDealPathTest {
    @Test public void policies() {
        assertTrue(EmberDealPath.shouldLive(EmberDealPath.OPEN));
        assertFalse(EmberDealPath.shouldLive(EmberDealPath.GATE));
        assertTrue(EmberDealPath.shouldMute(EmberDealPath.BUSY));
        assertEquals(EmberDealPath.GATE, EmberDealPath.parse("汇总"));
    }
}
