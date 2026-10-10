package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberTicketPathTest {
    @Test public void policies() {
        assertTrue(EmberTicketPath.shouldAuto(EmberTicketPath.AUTO));
        assertFalse(EmberTicketPath.shouldAuto(EmberTicketPath.ASK));
        assertTrue(EmberTicketPath.shouldMute(EmberTicketPath.MUTE));
        assertEquals(EmberTicketPath.AUTO, EmberTicketPath.parse("自动"));
    }
}
