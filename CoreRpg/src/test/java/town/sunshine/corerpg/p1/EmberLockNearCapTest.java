package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class EmberLockNearCapTest {

    static EmberAffix.Rules rules() {
        Map<String, Object> aff = new LinkedHashMap<String, Object>();
        Map<String, Object> one = new LinkedHashMap<String, Object>();
        one.put("id", "atk"); one.put("code", 1); one.put("name", "锋刃");
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

    @Test
    public void lockNearCap_forcesWhenOldIsCapMinus1() {
        EmberAffix.Rules r = rules();
        int cap = r.cap(2);
        assertEquals(3, cap);
        EmberAffix.Def d = r.def("atk");
        int oldEnc = EmberAffix.encode(d, cap - 1);
        EmberAffix.Roll low = new EmberAffix.Roll("atk", 1, 2, false);
        EmberAffix.Roll out = EmberLockNearCap.apply(low, oldEnc, 2, r, true);
        assertEquals(cap, out.tier);
        assertTrue(out.forced);
        assertEquals(0, out.pityAfter);
        assertTrue(EmberLockNearCap.wouldForce(oldEnc, 2, r, true));
    }

    @Test
    public void noForce_whenNotLockOrNotNear() {
        EmberAffix.Rules r = rules();
        EmberAffix.Def d = r.def("atk");
        EmberAffix.Roll low = new EmberAffix.Roll("atk", 1, 2, false);
        assertSame(low, EmberLockNearCap.apply(low, EmberAffix.encode(d, 1), 2, r, true));
        assertSame(low, EmberLockNearCap.apply(low, EmberAffix.encode(d, 2), 2, r, false));
        assertFalse(EmberLockNearCap.wouldForce(EmberAffix.encode(d, 1), 2, r, true));
    }
}
