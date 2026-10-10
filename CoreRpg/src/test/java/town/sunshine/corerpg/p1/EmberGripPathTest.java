package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGripPathTest {
    @Test public void policies() {
        assertTrue(EmberGripPath.shouldAuto(EmberGripPath.AUTO));
        assertTrue(EmberGripPath.shouldMute(EmberGripPath.MUTE));
        assertEquals(EmberGripPath.ASK, EmberGripPath.parse("提醒"));
    }
}
