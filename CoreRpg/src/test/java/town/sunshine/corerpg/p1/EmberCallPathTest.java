package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCallPathTest {
    @Test public void policies() {
        assertTrue(EmberCallPath.shouldAuto(EmberCallPath.AUTO));
        assertTrue(EmberCallPath.shouldMute(EmberCallPath.MUTE));
        assertEquals(EmberCallPath.ASK, EmberCallPath.parse("提醒"));
        assertEquals("auto", EmberCallPath.key(EmberCallPath.AUTO));
    }
}
