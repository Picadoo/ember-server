package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGlowPathTest {
    @Test public void policies() {
        assertTrue(EmberGlowPath.shouldAuto(EmberGlowPath.AUTO));
        assertFalse(EmberGlowPath.shouldAuto(EmberGlowPath.ASK));
        assertTrue(EmberGlowPath.shouldMute(EmberGlowPath.MUTE));
        assertEquals(EmberGlowPath.AUTO, EmberGlowPath.parse("自动"));
    }
}
