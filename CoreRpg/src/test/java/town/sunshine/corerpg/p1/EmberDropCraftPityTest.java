package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberDropCraftPityTest {

    private EmberRunRules.Grant item(int craft) {
        return new EmberRunRules.Grant("base_item", EmberRunRules.Kind.ITEM, "u", 1,
                new EmberRunRules.ItemRoll("scorch", "blade", 1, 0, craft));
    }

    @Test
    public void forcesFloorAfterNeed() {
        PlayerData d = new PlayerData();
        for (int i = 0; i < EmberDropCraftPity.NEED; i++) {
            List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
            g.add(item(0));
            assertFalse(EmberDropCraftPity.applyToGrants(d, g));
            assertEquals(0, g.get(0).item.craft);
        }
        assertEquals(EmberDropCraftPity.NEED, EmberDropCraftPity.get(d));
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(item(0));
        assertTrue(EmberDropCraftPity.applyToGrants(d, g));
        assertEquals(1, g.get(0).item.craft);
        assertEquals(0, EmberDropCraftPity.get(d));
    }

    @Test
    public void naturalCraftClears() {
        PlayerData d = new PlayerData();
        EmberDropCraftPity.applyToGrants(d, Collections.singletonList(item(0)));
        EmberDropCraftPity.applyToGrants(d, Collections.singletonList(item(0)));
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(item(2));
        assertFalse(EmberDropCraftPity.applyToGrants(d, g));
        assertEquals(0, EmberDropCraftPity.get(d));
    }
}
