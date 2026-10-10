package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSightPathTest {
    @Test public void policies() {
        assertTrue(EmberSightPath.shouldAuto(EmberSightPath.AUTO));
        assertTrue(EmberSightPath.shouldMute(EmberSightPath.MUTE));
        assertEquals(EmberSightPath.ASK, EmberSightPath.parse("提醒"));
        assertEquals(20 * 60 * 5, EmberSightPath.NV_TICKS);
    }
}
