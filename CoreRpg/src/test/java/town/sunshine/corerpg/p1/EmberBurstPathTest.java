package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBurstPathTest {
    @Test public void policies() {
        assertTrue(EmberBurstPath.shouldAuto(EmberBurstPath.AUTO));
        assertTrue(EmberBurstPath.shouldMute(EmberBurstPath.MUTE));
        assertEquals(EmberBurstPath.ASK, EmberBurstPath.parse("提醒"));
        assertEquals(60, EmberRunService.BURST_TICKS);
    }
}
