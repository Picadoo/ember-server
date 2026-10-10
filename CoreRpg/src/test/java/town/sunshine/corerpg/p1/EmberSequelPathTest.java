package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSequelPathTest {
    @Test public void policies() {
        assertTrue(EmberSequelPath.shouldAuto(EmberSequelPath.AUTO));
        assertTrue(EmberSequelPath.shouldMute(EmberSequelPath.MUTE));
        assertEquals(EmberSequelPath.ASK, EmberSequelPath.parse("提醒"));
        assertEquals("auto", EmberSequelPath.key(EmberSequelPath.AUTO));
    }
}
