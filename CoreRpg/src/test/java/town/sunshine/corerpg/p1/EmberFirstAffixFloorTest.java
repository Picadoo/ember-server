package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class EmberFirstAffixFloorTest {

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
    public void emptySlot_floorsTier1To2() {
        EmberAffix.Rules r = rules();
        EmberAffix.Roll low = new EmberAffix.Roll("atk", 1, 2, false);
        EmberAffix.Roll out = EmberFirstAffixFloor.apply(low, 0, 2, r);
        assertEquals(2, out.tier);
    }

    @Test
    public void filledSlot_unchanged() {
        EmberAffix.Rules r = rules();
        EmberAffix.Roll low = new EmberAffix.Roll("atk", 1, 2, false);
        int old = EmberAffix.encode(r.def("atk"), 1);
        assertSame(low, EmberFirstAffixFloor.apply(low, old, 2, r));
    }
}
