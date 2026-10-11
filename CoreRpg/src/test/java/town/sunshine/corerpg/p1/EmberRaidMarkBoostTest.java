package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRaidMarkBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        List<EmberRunRules.Grant> g = new ArrayList<EmberRunRules.Grant>();
        g.add(new EmberRunRules.Grant("raid_mark", EmberRunRules.Kind.MARK, "3", 1, null));
        assertTrue(EmberRaidMarkBoost.apply(d, g));
        assertEquals(2, g.get(0).amount);
        assertFalse(EmberRaidMarkBoost.apply(d, g));
    }
}
