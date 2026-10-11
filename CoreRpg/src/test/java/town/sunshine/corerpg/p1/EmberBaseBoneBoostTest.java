package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBaseBoneBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("base_bone", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BONE, 1, null));
        assertTrue(EmberBaseBoneBoost.apply(d, g));
        assertEquals(2, g.get(0).amount);
        assertFalse(EmberBaseBoneBoost.apply(d, g));
    }
}
