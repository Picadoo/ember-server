package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGapPathTest {
    @Test public void policies() {
        assertTrue(EmberGapPath.shouldAuto(EmberGapPath.AUTO));
        assertTrue(EmberGapPath.shouldMute(EmberGapPath.MUTE));
        assertEquals(EmberGapPath.ASK, EmberGapPath.parse("提醒"));
        assertEquals("auto", EmberGapPath.key(EmberGapPath.AUTO));
    }
}
