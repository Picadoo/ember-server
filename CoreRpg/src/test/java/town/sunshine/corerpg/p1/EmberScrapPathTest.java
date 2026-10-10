package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberScrapPathTest {
    @Test public void policies() {
        assertTrue(EmberScrapPath.shouldAuto(EmberScrapPath.AUTO));
        assertTrue(EmberScrapPath.shouldMute(EmberScrapPath.MUTE));
        assertEquals(EmberScrapPath.ASK, EmberScrapPath.parse("提醒"));
        assertEquals("auto", EmberScrapPath.key(EmberScrapPath.AUTO));
    }
}
