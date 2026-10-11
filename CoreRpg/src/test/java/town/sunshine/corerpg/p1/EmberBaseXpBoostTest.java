package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBaseXpBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("base_xp", EmberRunRules.Kind.XP, null, 50, null));
        assertTrue(EmberBaseXpBoost.apply(d, g));
        assertEquals(60, g.get(0).amount);
        assertFalse(EmberBaseXpBoost.apply(d, g));
    }
}
