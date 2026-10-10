package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberShiftPathTest {
    @Test public void policies() {
        assertTrue(EmberShiftPath.shouldAuto(EmberShiftPath.AUTO));
        assertTrue(EmberShiftPath.shouldMute(EmberShiftPath.MUTE));
        assertEquals(EmberShiftPath.ASK, EmberShiftPath.parse("提醒"));
        assertEquals("auto", EmberShiftPath.key(EmberShiftPath.AUTO));
    }
}
