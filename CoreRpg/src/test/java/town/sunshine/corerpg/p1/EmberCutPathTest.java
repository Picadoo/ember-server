package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCutPathTest {
    @Test public void policies() {
        assertTrue(EmberCutPath.shouldAuto(EmberCutPath.AUTO));
        assertTrue(EmberCutPath.shouldMute(EmberCutPath.MUTE));
        assertEquals(EmberCutPath.ASK, EmberCutPath.parse("提醒"));
        assertEquals("auto", EmberCutPath.key(EmberCutPath.AUTO));
    }
}
