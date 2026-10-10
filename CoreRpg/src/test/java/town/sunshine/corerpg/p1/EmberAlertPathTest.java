package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberAlertPathTest {
    @Test public void policies() {
        assertTrue(EmberAlertPath.shouldAuto(EmberAlertPath.AUTO));
        assertTrue(EmberAlertPath.shouldMute(EmberAlertPath.MUTE));
        assertEquals(EmberAlertPath.ASK, EmberAlertPath.parse("提醒"));
        assertEquals("auto", EmberAlertPath.key(EmberAlertPath.AUTO));
    }
}
