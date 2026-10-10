package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGuardPathTest {
    @Test public void policies() {
        assertTrue(EmberGuardPath.shouldAuto(EmberGuardPath.AUTO));
        assertTrue(EmberGuardPath.shouldMute(EmberGuardPath.MUTE));
        assertEquals(EmberGuardPath.ASK, EmberGuardPath.parse("提醒"));
        assertEquals("auto", EmberGuardPath.key(EmberGuardPath.AUTO));
    }
}
