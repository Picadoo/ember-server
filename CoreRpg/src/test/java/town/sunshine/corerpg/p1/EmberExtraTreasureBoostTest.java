package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberExtraTreasureBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("extra_treasure_coin", EmberRunRules.Kind.COIN, null, 100, null));
        assertTrue(EmberExtraTreasureBoost.apply(d, g));
        assertEquals(200, g.get(0).amount);
        assertFalse(EmberExtraTreasureBoost.apply(d, g));
    }
}
