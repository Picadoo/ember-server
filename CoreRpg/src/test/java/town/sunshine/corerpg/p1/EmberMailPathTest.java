package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberMailPathTest {
    @Test public void policies() {
        assertTrue(EmberMailPath.shouldAuto(EmberMailPath.AUTO));
        assertFalse(EmberMailPath.shouldAuto(EmberMailPath.ASK));
        assertTrue(EmberMailPath.shouldMute(EmberMailPath.MUTE));
        assertEquals(EmberMailPath.AUTO, EmberMailPath.parse("自动"));
    }
}
