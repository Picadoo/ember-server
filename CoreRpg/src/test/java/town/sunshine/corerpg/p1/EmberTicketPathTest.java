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
    @Test public void tipFrOfferPickConstants() {
        assertEquals("/ember_p1_ticket_path", EmberTicketPath.PATH_PICK_CMD);
        assertEquals("[旧票路径]", EmberTicketPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberTicketPath.PATH_ADVENTURE_CMD);
        assertEquals("旧票路径 · 旧票·自动", EmberTicketPath.menuLine(EmberTicketPath.AUTO));
        String g = EmberTicketPath.glance(EmberTicketPath.NONE, true);
        assertTrue(g.contains("点旧票路径页"));
    }
}
