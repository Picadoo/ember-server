package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBaseShardBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("base_shard", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, 2, null));
        assertTrue(EmberBaseShardBoost.apply(d, g));
        assertEquals(3, g.get(0).amount);
        assertFalse(EmberBaseShardBoost.apply(d, g));
    }
}
