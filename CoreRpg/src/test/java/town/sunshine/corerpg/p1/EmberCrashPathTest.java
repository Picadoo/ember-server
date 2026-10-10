package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCrashPathTest {
    @Test public void policies() {
        assertTrue(EmberCrashPath.shouldAuto(EmberCrashPath.AUTO));
        assertTrue(EmberCrashPath.shouldMute(EmberCrashPath.MUTE));
        assertEquals(EmberCrashPath.ASK, EmberCrashPath.parse("提醒"));
        assertEquals("auto", EmberCrashPath.key(EmberCrashPath.AUTO));
    }
}
