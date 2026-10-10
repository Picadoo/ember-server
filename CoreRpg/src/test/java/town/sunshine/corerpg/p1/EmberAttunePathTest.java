package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberAttunePathTest {
    @Test public void parse_labels() {
        assertEquals(EmberAttunePath.ORIGIN, EmberAttunePath.parse("原版"));
        assertEquals(EmberAttunePath.ALT, EmberAttunePath.parse("调律"));
        assertEquals(EmberAttunePath.NONE, EmberAttunePath.parse("clear"));
        assertTrue(EmberAttunePath.tip(EmberAttunePath.ALT).contains("调律"));
        assertTrue(EmberAttunePath.glance(EmberAttunePath.ORIGIN, true).contains("原版"));
        assertFalse(EmberSignature.ALTS.isEmpty());
    }
}
