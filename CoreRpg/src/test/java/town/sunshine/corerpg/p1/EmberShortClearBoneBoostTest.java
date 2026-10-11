package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberShortClearBoneBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("sx03_clear_bone", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BONE, 1, null));
        assertTrue(EmberShortClearBoneBoost.apply(d, g));
        assertEquals(2, g.get(0).amount);
        assertFalse(EmberShortClearBoneBoost.apply(d, g));
    }
}
