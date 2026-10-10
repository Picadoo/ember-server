package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFocusPathTest {
    @Test public void policies() {
        assertTrue(EmberFocusPath.shouldAuto(EmberFocusPath.AUTO));
        assertTrue(EmberFocusPath.shouldMute(EmberFocusPath.MUTE));
        assertEquals(EmberFocusPath.ASK, EmberFocusPath.parse("提醒"));
        assertEquals("auto", EmberFocusPath.key(EmberFocusPath.AUTO));
    }
}
