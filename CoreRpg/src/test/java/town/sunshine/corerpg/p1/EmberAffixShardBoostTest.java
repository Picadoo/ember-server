package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAffixShardBoostTest {
    @Test public void boostOnce() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("var_affix_shard", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, 2, null));
        assertTrue(EmberAffixShardBoost.apply(d, true, g));
        assertEquals(2, g.size());
        assertFalse(EmberAffixShardBoost.apply(d, true, g));
    }
    @Test public void noAffixNoBoost() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        assertFalse(EmberAffixShardBoost.apply(d, true, g));
        assertFalse(EmberAffixShardBoost.apply(d, false, g));
    }
}
