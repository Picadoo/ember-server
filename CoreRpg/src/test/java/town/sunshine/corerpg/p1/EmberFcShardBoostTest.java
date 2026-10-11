package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFcShardBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("fc_q02_shard", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, 3, null));
        assertTrue(EmberFcShardBoost.apply(d, g));
        assertEquals(4, g.get(0).amount);
        assertFalse(EmberFcShardBoost.apply(d, g));
    }
}
