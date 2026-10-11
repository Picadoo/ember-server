package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberShortClearCoinBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("sx_clear_coin", EmberRunRules.Kind.COIN, null, 40, null));
        assertTrue(EmberShortClearCoinBoost.apply(d, g));
        assertEquals(70, g.get(0).amount);
        assertFalse(EmberShortClearCoinBoost.apply(d, g));
    }
}
