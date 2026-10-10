package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSetFocusTest {
    @Test public void parse_and_glance() {
        assertEquals(EmberSetFocus.SCORCH, EmberSetFocus.parse("焚烬"));
        assertEquals(EmberSetFocus.BURST, EmberSetFocus.parse("burst"));
        assertEquals(EmberSetFocus.NONE, EmberSetFocus.parse("clear"));
        assertEquals("焚烬", EmberSetFocus.label(EmberSetFocus.SCORCH));
        assertTrue(EmberSetFocus.glance(EmberSetFocus.SCORCH, "scorch", 1, "scorch").contains("穿着"));
        assertTrue(EmberSetFocus.glance(EmberSetFocus.BURST, "none", 0, "scorch").contains("掉落"));
    }
}
