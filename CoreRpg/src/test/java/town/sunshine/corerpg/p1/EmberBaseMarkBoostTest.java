package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBaseMarkBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("base_mark", EmberRunRules.Kind.MARK, "1", 1, null));
        assertTrue(EmberBaseMarkBoost.apply(d, g));
        assertEquals(2, g.get(0).amount);
        assertFalse(EmberBaseMarkBoost.apply(d, g));
    }
}
