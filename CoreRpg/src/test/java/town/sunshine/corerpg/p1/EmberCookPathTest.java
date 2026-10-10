package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCookPathTest {
    @Test public void policies() {
        assertTrue(EmberCookPath.shouldAuto(EmberCookPath.AUTO));
        assertTrue(EmberCookPath.shouldMute(EmberCookPath.MUTE));
        assertEquals(EmberCookPath.ASK, EmberCookPath.parse("提醒"));
    }
}
