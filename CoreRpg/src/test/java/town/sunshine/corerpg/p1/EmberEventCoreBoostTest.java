package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberEventCoreBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("var_event_core", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_CORE, 1, null));
        assertTrue(EmberEventCoreBoost.apply(d, true, g));
        assertFalse(EmberEventCoreBoost.apply(d, true, g));
        assertEquals(2, g.size());
    }
}
