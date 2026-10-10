package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBoltPathTest {
    @Test public void policies() {
        assertTrue(EmberBoltPath.shouldAuto(EmberBoltPath.AUTO));
        assertTrue(EmberBoltPath.shouldMute(EmberBoltPath.MUTE));
        assertEquals(EmberBoltPath.ASK, EmberBoltPath.parse("提醒"));
        assertEquals("auto", EmberBoltPath.key(EmberBoltPath.AUTO));
    }
}
