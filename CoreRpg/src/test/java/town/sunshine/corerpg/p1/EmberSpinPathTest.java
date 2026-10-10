package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSpinPathTest {
    @Test public void policies() {
        assertTrue(EmberSpinPath.shouldAuto(EmberSpinPath.AUTO));
        assertTrue(EmberSpinPath.shouldMute(EmberSpinPath.MUTE));
        assertEquals(EmberSpinPath.ASK, EmberSpinPath.parse("提醒"));
        assertEquals("auto", EmberSpinPath.key(EmberSpinPath.AUTO));
    }
}
