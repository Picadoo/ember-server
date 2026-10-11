package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFcCoinBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("fc_q01_coin", EmberRunRules.Kind.COIN, null, 100, null));
        assertTrue(EmberFcCoinBoost.apply(d, g));
        assertEquals(150, g.get(0).amount);
        assertFalse(EmberFcCoinBoost.apply(d, g));
    }
}
