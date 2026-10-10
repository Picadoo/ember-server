package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberStashPathTest {
    @Test public void policies() {
        assertTrue(EmberStashPath.shouldAuto(EmberStashPath.AUTO));
        assertFalse(EmberStashPath.shouldAuto(EmberStashPath.ASK));
        assertTrue(EmberStashPath.shouldMute(EmberStashPath.MUTE));
        assertEquals(EmberStashPath.AUTO, EmberStashPath.parse("自动"));
    }
}
