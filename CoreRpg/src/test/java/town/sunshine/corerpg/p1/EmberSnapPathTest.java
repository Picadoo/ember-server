package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSnapPathTest {
    @Test public void policies() {
        assertTrue(EmberSnapPath.shouldAuto(EmberSnapPath.AUTO));
        assertTrue(EmberSnapPath.shouldMute(EmberSnapPath.MUTE));
        assertEquals(EmberSnapPath.ASK, EmberSnapPath.parse("提醒"));
        assertEquals("auto", EmberSnapPath.key(EmberSnapPath.AUTO));
    }
}
