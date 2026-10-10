package town.sunshine.corerpg.p1;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBountyPathTest {
    @Test public void ensure_affix_when_missing() {
        List<String> aff = Arrays.asList("blazing", "frost");
        List<String> ev = Arrays.asList("hold", "timed");
        String[] in = {"", "", "r2", "hold"};
        String[] out = EmberBountyPath.applyEnsure(in, EmberBountyPath.AFFIX, aff, ev, 42L);
        assertFalse(out[1].isEmpty());
        assertTrue(aff.contains(out[1]));
        assertEquals("hold", out[3]); // event untouched
    }
    @Test public void ensure_event_when_missing() {
        List<String> aff = Arrays.asList("blazing");
        List<String> ev = Arrays.asList("hold", "breach");
        String[] in = {"r1", "blazing", "", ""};
        String[] out = EmberBountyPath.applyEnsure(in, EmberBountyPath.EVENT, aff, ev, 7L);
        assertFalse(out[2].isEmpty());
        assertTrue(ev.contains(out[3]));
        assertEquals("blazing", out[1]);
    }
    @Test public void both_noop_and_keeps_existing() {
        String[] in = {"r1", "blazing", "", ""};
        String[] out = EmberBountyPath.applyEnsure(in, EmberBountyPath.BOTH, Arrays.asList("frost"), Arrays.asList("hold"), 1L);
        assertEquals("blazing", out[1]);
        assertEquals("", out[2]);
        String[] kept = EmberBountyPath.applyEnsure(in, EmberBountyPath.AFFIX, Arrays.asList("frost"), Collections.<String>emptyList(), 1L);
        assertEquals("blazing", kept[1]); // already present — no remap
    }
    @Test public void parse() {
        assertEquals(EmberBountyPath.AFFIX, EmberBountyPath.parse("词缀"));
        assertEquals(EmberBountyPath.EVENT, EmberBountyPath.parse("事件"));
        assertEquals(EmberBountyPath.BOTH, EmberBountyPath.parse("双追"));
    }
}
