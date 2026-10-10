package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBeaconPathTest {
    @Test public void policies() {
        assertTrue(EmberBeaconPath.shouldAuto(EmberBeaconPath.AUTO));
        assertTrue(EmberBeaconPath.shouldMute(EmberBeaconPath.MUTE));
        assertEquals(EmberBeaconPath.ASK, EmberBeaconPath.parse("提醒"));
        assertEquals("auto", EmberBeaconPath.key(EmberBeaconPath.AUTO));
    }
}
