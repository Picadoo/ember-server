package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBaitPathTest {
    @Test public void policies() {
        assertTrue(EmberBaitPath.shouldAuto(EmberBaitPath.AUTO));
        assertTrue(EmberBaitPath.shouldMute(EmberBaitPath.MUTE));
        assertEquals(EmberBaitPath.ASK, EmberBaitPath.parse("提醒"));
        assertEquals("auto", EmberBaitPath.key(EmberBaitPath.AUTO));
    }
}
