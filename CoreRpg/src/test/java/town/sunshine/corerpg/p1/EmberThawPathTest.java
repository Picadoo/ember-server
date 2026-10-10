package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberThawPathTest {
    @Test public void policies() {
        assertTrue(EmberThawPath.shouldAuto(EmberThawPath.AUTO));
        assertTrue(EmberThawPath.shouldMute(EmberThawPath.MUTE));
        assertEquals(EmberThawPath.ASK, EmberThawPath.parse("提醒"));
        assertEquals("auto", EmberThawPath.key(EmberThawPath.AUTO));
    }
}
