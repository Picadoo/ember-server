package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRallyPathTest {
    @Test public void policies() {
        assertTrue(EmberRallyPath.shouldAuto(EmberRallyPath.AUTO));
        assertTrue(EmberRallyPath.shouldMute(EmberRallyPath.MUTE));
        assertEquals(EmberRallyPath.ASK, EmberRallyPath.parse("提醒"));
        assertEquals(80, EmberRunService.RALLY_TICKS);
    }
}
