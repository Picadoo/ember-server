package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberExtraChestFloorSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("extra_chest_item", EmberRunRules.Kind.ITEM, "u", 1,
                new EmberRunRules.ItemRoll("ember", "blade", 1, 0, 0)));
        assertTrue(EmberExtraChestFloorSoft.apply(d, g));
        assertEquals(1, g.get(0).item.quality);
        assertFalse(EmberExtraChestFloorSoft.apply(d, g));
    }
}
