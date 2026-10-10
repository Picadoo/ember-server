package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberLinkPathTest {
    @Test public void policies() {
        assertTrue(EmberLinkPath.shouldAuto(EmberLinkPath.AUTO));
        assertTrue(EmberLinkPath.shouldMute(EmberLinkPath.MUTE));
        assertEquals(EmberLinkPath.ASK, EmberLinkPath.parse("提醒"));
        assertEquals("auto", EmberLinkPath.key(EmberLinkPath.AUTO));
    }
}
