package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberShellPathTest {
    @Test public void policies() {
        assertTrue(EmberShellPath.shouldAuto(EmberShellPath.AUTO));
        assertTrue(EmberShellPath.shouldMute(EmberShellPath.MUTE));
        assertEquals(EmberShellPath.ASK, EmberShellPath.parse("提醒"));
        assertEquals("auto", EmberShellPath.key(EmberShellPath.AUTO));
    }
}
