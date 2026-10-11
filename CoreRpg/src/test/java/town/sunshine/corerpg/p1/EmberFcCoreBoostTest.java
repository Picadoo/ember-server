package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFcCoreBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("fc_q03_core", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_CORE, 1, null));
        assertTrue(EmberFcCoreBoost.apply(d, g));
        assertEquals(2, g.get(0).amount);
        assertFalse(EmberFcCoreBoost.apply(d, g));
    }
}
