package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSignPathTest {
    @Test public void policies() {
        assertTrue(EmberSignPath.shouldAuto(EmberSignPath.AUTO));
        assertFalse(EmberSignPath.shouldAuto(EmberSignPath.ASK));
        assertTrue(EmberSignPath.shouldMute(EmberSignPath.MUTE));
        assertEquals(EmberSignPath.AUTO, EmberSignPath.parse("自动"));
        assertEquals(EmberSignPath.MUTE, EmberSignPath.parse("静默"));
    }
}
