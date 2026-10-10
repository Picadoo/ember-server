package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberTitlePathTest {
    @Test public void policies() {
        assertTrue(EmberTitlePath.shouldAuto(EmberTitlePath.AUTO));
        assertFalse(EmberTitlePath.shouldAuto(EmberTitlePath.ASK));
        assertTrue(EmberTitlePath.shouldMute(EmberTitlePath.MUTE));
        assertEquals(EmberTitlePath.AUTO, EmberTitlePath.parse("自动"));
    }
}
