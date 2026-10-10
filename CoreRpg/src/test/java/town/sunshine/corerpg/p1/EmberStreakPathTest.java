package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberStreakPathTest {
    @Test public void policies() {
        assertTrue(EmberStreakPath.shouldAuto(EmberStreakPath.AUTO));
        assertTrue(EmberStreakPath.shouldMute(EmberStreakPath.MUTE));
        assertEquals(EmberStreakPath.ASK, EmberStreakPath.parse("提醒"));
        assertEquals("auto", EmberStreakPath.key(EmberStreakPath.AUTO));
    }
}
