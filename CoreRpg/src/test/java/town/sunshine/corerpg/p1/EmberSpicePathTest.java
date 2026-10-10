package town.sunshine.corerpg.p1;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSpicePathTest {
    static final List<String> AFFIX = Arrays.asList(
            "blazing", "frost", "shield", "mortar", "jailer", "molten", "venom");
    static final List<String> EVENT = Arrays.asList(
            "timed", "crystal", "hold", "beacon", "escort");

    @Test public void parse_labels() {
        assertEquals(EmberSpicePath.BLAZE, EmberSpicePath.parse("火力"));
        assertEquals(EmberSpicePath.CONTROL, EmberSpicePath.parse("control"));
        assertEquals(EmberSpicePath.OBJECTIVE, EmberSpicePath.parse("目标"));
        assertTrue(EmberSpicePath.tip(EmberSpicePath.BLAZE).contains("词缀"));
    }

    @Test public void bias_blaze_remaps_control_affix() {
        String[] rolled = {"r1", "frost", "", ""};
        String[] out = EmberSpicePath.applyBias(rolled, EmberSpicePath.BLAZE, AFFIX, EVENT, 42L);
        assertEquals("r1", out[0]);
        assertTrue(EmberSpicePath.BLAZE_AFFIX.contains(out[1]));
        assertFalse(EmberSpicePath.CONTROL_AFFIX.contains(out[1]));
    }

    @Test public void bias_blaze_injects_when_empty() {
        String[] rolled = {"", "", "r2", "timed"};
        String[] out = EmberSpicePath.applyBias(rolled, EmberSpicePath.BLAZE, AFFIX, EVENT, 7L);
        assertFalse(out[0].isEmpty());
        assertTrue(EmberSpicePath.BLAZE_AFFIX.contains(out[1]));
        assertEquals("r2", out[2]);
        assertEquals("timed", out[3]);
    }

    @Test public void bias_objective_remaps_timed() {
        String[] rolled = {"r1", "blazing", "r3", "timed"};
        String[] out = EmberSpicePath.applyBias(rolled, EmberSpicePath.OBJECTIVE, AFFIX, EVENT, 99L);
        assertEquals("r3", out[2]);
        assertTrue(EmberSpicePath.OBJECTIVE_EVENT.contains(out[3]));
        assertEquals("blazing", out[1]); // affix untouched
    }

    @Test public void bias_none_noop() {
        String[] rolled = {"r2", "frost", "r1", "timed"};
        String[] out = EmberSpicePath.applyBias(rolled, EmberSpicePath.NONE, AFFIX, EVENT, 1L);
        assertArrayEquals(rolled, out);
    }
}
