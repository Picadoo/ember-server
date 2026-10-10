package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFleePathTest {
    @Test public void policies() {
        assertTrue(EmberFleePath.shouldAuto(EmberFleePath.AUTO));
        assertTrue(EmberFleePath.shouldMute(EmberFleePath.MUTE));
        assertEquals(EmberFleePath.ASK, EmberFleePath.parse("提醒"));
        assertEquals("auto", EmberFleePath.key(EmberFleePath.AUTO));
    }
}
