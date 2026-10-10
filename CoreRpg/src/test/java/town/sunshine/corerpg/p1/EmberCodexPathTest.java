package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCodexPathTest {
    @Test public void policies() {
        assertTrue(EmberCodexPath.shouldAuto(EmberCodexPath.AUTO));
        assertFalse(EmberCodexPath.shouldAuto(EmberCodexPath.ASK));
        assertTrue(EmberCodexPath.shouldMute(EmberCodexPath.MUTE));
        assertEquals(EmberCodexPath.AUTO, EmberCodexPath.parse("自动"));
    }
}
