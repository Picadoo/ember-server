package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberExtraEliteBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("extra_elite_shard", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, 2, null));
        g.add(new EmberRunRules.Grant("extra_elite_core", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_CORE, 1, null));
        assertTrue(EmberExtraEliteBoost.apply(d, g));
        assertEquals(4, g.get(0).amount);
        assertEquals(2, g.get(1).amount);
        assertFalse(EmberExtraEliteBoost.apply(d, g));
    }
}
