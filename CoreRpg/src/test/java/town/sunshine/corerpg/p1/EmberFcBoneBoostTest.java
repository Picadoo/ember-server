package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFcBoneBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("fc_q04_bone", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_BONE, 2, null));
        assertTrue(EmberFcBoneBoost.apply(d, g));
        assertEquals(3, g.get(0).amount);
        assertFalse(EmberFcBoneBoost.apply(d, g));
    }
}
