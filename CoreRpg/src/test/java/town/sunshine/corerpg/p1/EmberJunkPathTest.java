package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberJunkPathTest {
    @Test public void policies() {
        assertTrue(EmberJunkPath.shouldAuto(EmberJunkPath.AUTO));
        assertFalse(EmberJunkPath.shouldAuto(EmberJunkPath.ASK));
        assertTrue(EmberJunkPath.shouldMute(EmberJunkPath.MUTE));
        assertEquals(EmberJunkPath.AUTO, EmberJunkPath.parse("自动"));
    }
}
