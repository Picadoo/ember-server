package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFreePathTest {
    @Test public void policies() {
        assertTrue(EmberFreePath.shouldAuto(EmberFreePath.AUTO));
        assertTrue(EmberFreePath.shouldMute(EmberFreePath.MUTE));
        assertEquals(EmberFreePath.ASK, EmberFreePath.parse("提醒"));
        assertEquals("auto", EmberFreePath.key(EmberFreePath.AUTO));
        assertEquals(128, EmberFreePath.JAILER_JUMP_AMP);
    }
}
