package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFeedPathTest {
    @Test public void policies() {
        assertTrue(EmberFeedPath.shouldAuto(EmberFeedPath.AUTO));
        assertFalse(EmberFeedPath.shouldAuto(EmberFeedPath.ASK));
        assertTrue(EmberFeedPath.shouldMute(EmberFeedPath.MUTE));
        assertEquals(EmberFeedPath.AUTO, EmberFeedPath.parse("喂养"));
    }
}
