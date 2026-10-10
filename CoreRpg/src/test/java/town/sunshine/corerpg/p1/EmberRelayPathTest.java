package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRelayPathTest {
    @Test public void policies() {
        assertTrue(EmberRelayPath.shouldAuto(EmberRelayPath.AUTO));
        assertTrue(EmberRelayPath.shouldMute(EmberRelayPath.MUTE));
        assertEquals(EmberRelayPath.ASK, EmberRelayPath.parse("提醒"));
        assertEquals("auto", EmberRelayPath.key(EmberRelayPath.AUTO));
    }
}
