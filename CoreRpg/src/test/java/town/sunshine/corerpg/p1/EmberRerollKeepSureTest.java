package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
public class EmberRerollKeepSureTest {
    @Test public void nullSafe() {
        assertFalse(EmberRerollKeepSure.autoKeepNew(1, 0, 0, false, null));
    }
    @Test public void higherTierKeeps() {
        EmberAffix.Rules r = rules();
        assertNotNull(r);
        int oldE = EmberAffix.encode(r.defs.get(0), 1);
        int newE = EmberAffix.encode(r.defs.get(0), 2);
        assertTrue(EmberRerollKeepSure.autoKeepNew(oldE, newE, 3, false, r));
        assertFalse(EmberRerollKeepSure.autoKeepNew(newE, oldE, 3, false, r));
    }
    @Test public void forcedEqualKeeps() {
        EmberAffix.Rules r = rules();
        int e = EmberAffix.encode(r.defs.get(0), 2);
        assertTrue(EmberRerollKeepSure.autoKeepNew(e, e, 3, true, r));
        assertFalse(EmberRerollKeepSure.autoKeepNew(e, e, 3, false, r));
    }
    static EmberAffix.Rules rules() {
        Map<String, Object> aff = new LinkedHashMap<String, Object>();
        Map<String, Object> one = new LinkedHashMap<String, Object>();
        one.put("id", "t"); one.put("code", 1); one.put("name", "测");
        one.put("key", "k"); one.put("desc", "d"); one.put("note", "");
        one.put("values", Arrays.asList(1.0, 2.0, 3.0));
        aff.put("blade", Collections.singletonList(one));
        Map<String, Object> rr = new LinkedHashMap<String, Object>();
        rr.put("pity", 10);
        rr.put("tier_weights", Arrays.asList(50, 30, 20));
        rr.put("tier_cap", Arrays.asList(1, 2, 3, 3));
        rr.put("coin", Arrays.asList(1, 1, 1, 1));
        rr.put("shard", Arrays.asList(1, 1, 1));
        rr.put("affixes", aff);
        return EmberAffix.parse(Collections.singletonMap("reroll", rr));
    }
}
