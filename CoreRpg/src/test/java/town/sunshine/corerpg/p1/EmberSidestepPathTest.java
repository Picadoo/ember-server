package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSidestepPathTest {
    @Test public void policies() {
        assertTrue(EmberSidestepPath.shouldAuto(EmberSidestepPath.AUTO));
        assertTrue(EmberSidestepPath.shouldMute(EmberSidestepPath.MUTE));
        assertEquals(EmberSidestepPath.ASK, EmberSidestepPath.parse("提醒"));
        assertEquals("auto", EmberSidestepPath.key(EmberSidestepPath.AUTO));
    }
}
