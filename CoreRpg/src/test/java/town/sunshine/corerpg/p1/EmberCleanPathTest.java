package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCleanPathTest {
    @Test public void policies() {
        assertTrue(EmberCleanPath.shouldAuto(EmberCleanPath.AUTO));
        assertTrue(EmberCleanPath.shouldMute(EmberCleanPath.MUTE));
        assertEquals(EmberCleanPath.ASK, EmberCleanPath.parse("提醒"));
        assertEquals("auto", EmberCleanPath.key(EmberCleanPath.AUTO));
    }
}
