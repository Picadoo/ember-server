package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDoomPathTest {
    @Test public void policies() {
        assertTrue(EmberDoomPath.shouldAuto(EmberDoomPath.AUTO));
        assertTrue(EmberDoomPath.shouldMute(EmberDoomPath.MUTE));
        assertEquals(EmberDoomPath.ASK, EmberDoomPath.parse("提醒"));
        assertEquals("auto", EmberDoomPath.key(EmberDoomPath.AUTO));
    }
}
