package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSlipPathTest {
    @Test public void policies() {
        assertTrue(EmberSlipPath.shouldAuto(EmberSlipPath.AUTO));
        assertTrue(EmberSlipPath.shouldMute(EmberSlipPath.MUTE));
        assertEquals(EmberSlipPath.ASK, EmberSlipPath.parse("提醒"));
        assertEquals("auto", EmberSlipPath.key(EmberSlipPath.AUTO));
    }
}
