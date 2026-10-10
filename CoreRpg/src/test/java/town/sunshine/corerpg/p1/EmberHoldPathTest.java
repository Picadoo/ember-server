package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberHoldPathTest {
    @Test public void policies() {
        assertTrue(EmberHoldPath.shouldAuto(EmberHoldPath.AUTO));
        assertTrue(EmberHoldPath.shouldMute(EmberHoldPath.MUTE));
        assertEquals(EmberHoldPath.ASK, EmberHoldPath.parse("提醒"));
        assertEquals("auto", EmberHoldPath.key(EmberHoldPath.AUTO));
    }
}
