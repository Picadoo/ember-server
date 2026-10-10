package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSoakPathTest {
    @Test public void policies() {
        assertTrue(EmberSoakPath.shouldAuto(EmberSoakPath.AUTO));
        assertTrue(EmberSoakPath.shouldMute(EmberSoakPath.MUTE));
        assertEquals(EmberSoakPath.ASK, EmberSoakPath.parse("提醒"));
        assertEquals("auto", EmberSoakPath.key(EmberSoakPath.AUTO));
    }
}
