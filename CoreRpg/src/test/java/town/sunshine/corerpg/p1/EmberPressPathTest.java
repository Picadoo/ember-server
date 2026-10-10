package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPressPathTest {
    @Test public void policies() {
        assertTrue(EmberPressPath.shouldAuto(EmberPressPath.AUTO));
        assertTrue(EmberPressPath.shouldMute(EmberPressPath.MUTE));
        assertEquals(EmberPressPath.ASK, EmberPressPath.parse("提醒"));
        assertEquals("auto", EmberPressPath.key(EmberPressPath.AUTO));
    }
}
