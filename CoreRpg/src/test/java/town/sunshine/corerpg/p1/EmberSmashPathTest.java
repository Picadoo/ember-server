package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSmashPathTest {
    @Test public void policies() {
        assertTrue(EmberSmashPath.shouldAuto(EmberSmashPath.AUTO));
        assertTrue(EmberSmashPath.shouldMute(EmberSmashPath.MUTE));
        assertEquals(EmberSmashPath.ASK, EmberSmashPath.parse("提醒"));
        assertEquals("auto", EmberSmashPath.key(EmberSmashPath.AUTO));
    }
}
