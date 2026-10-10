package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPhasePathTest {
    @Test public void policies() {
        assertTrue(EmberPhasePath.shouldAuto(EmberPhasePath.AUTO));
        assertTrue(EmberPhasePath.shouldMute(EmberPhasePath.MUTE));
        assertEquals(EmberPhasePath.ASK, EmberPhasePath.parse("提醒"));
        assertEquals("auto", EmberPhasePath.key(EmberPhasePath.AUTO));
    }
}
