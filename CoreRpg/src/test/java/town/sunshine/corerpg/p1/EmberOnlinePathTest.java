package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberOnlinePathTest {
    @Test public void policies() {
        assertTrue(EmberOnlinePath.shouldAutoClaim(EmberOnlinePath.AUTO));
        assertFalse(EmberOnlinePath.shouldAutoClaim(EmberOnlinePath.ASK));
        assertTrue(EmberOnlinePath.shouldMute(EmberOnlinePath.MUTE));
        assertFalse(EmberOnlinePath.shouldMute(EmberOnlinePath.ASK));
        assertEquals(EmberOnlinePath.AUTO, EmberOnlinePath.parse("自动"));
    }
}
