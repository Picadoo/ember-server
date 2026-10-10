package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRushPathTest {
    @Test public void policies() {
        assertTrue(EmberRushPath.shouldAuto(EmberRushPath.AUTO));
        assertTrue(EmberRushPath.shouldMute(EmberRushPath.MUTE));
        assertEquals(EmberRushPath.ASK, EmberRushPath.parse("提醒"));
        assertEquals("auto", EmberRushPath.key(EmberRushPath.AUTO));
    }
}
