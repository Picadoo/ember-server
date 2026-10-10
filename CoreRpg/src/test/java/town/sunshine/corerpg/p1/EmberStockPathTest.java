package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberStockPathTest {
    @Test public void policies() {
        assertTrue(EmberStockPath.shouldAuto(EmberStockPath.AUTO));
        assertTrue(EmberStockPath.shouldMute(EmberStockPath.MUTE));
        assertEquals(EmberStockPath.ASK, EmberStockPath.parse("提醒"));
        assertEquals("auto", EmberStockPath.key(EmberStockPath.AUTO));
        assertEquals(2, EmberStockPath.NEED);
    }
}
