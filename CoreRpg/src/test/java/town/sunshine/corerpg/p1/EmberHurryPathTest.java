package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberHurryPathTest {
    @Test public void policies() {
        assertTrue(EmberHurryPath.shouldAuto(EmberHurryPath.AUTO));
        assertTrue(EmberHurryPath.shouldMute(EmberHurryPath.MUTE));
        assertEquals(EmberHurryPath.ASK, EmberHurryPath.parse("提醒"));
        assertEquals("auto", EmberHurryPath.key(EmberHurryPath.AUTO));
    }
}
