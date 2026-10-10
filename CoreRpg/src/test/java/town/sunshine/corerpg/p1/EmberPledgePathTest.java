package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import town.sunshine.corerpg.PlayerData;
public class EmberPledgePathTest {
    @Test public void parse_and_apply_toggles() {
        assertEquals(EmberPledgePath.LEAN, EmberPledgePath.parse("限药"));
        assertEquals(EmberPledgePath.REVERSE, EmberPledgePath.parse("reverse"));
        assertEquals(EmberPledgePath.BOTH, EmberPledgePath.parse("双挂"));
        assertEquals(EmberPledgePath.NONE, EmberPledgePath.parse("clear"));
        assertTrue(EmberPledgePath.label(EmberPledgePath.LEAN).contains("限药"));

        PlayerData d = new PlayerData();
        java.util.List<String> pool = Arrays.asList("lean", "reverse");
        EmberPledgePath.apply(d, EmberPledgePath.LEAN, pool);
        assertTrue(EmberPledgeService.isOn(d, "lean"));
        assertFalse(EmberPledgeService.isOn(d, "reverse"));
        assertEquals(EmberPledgePath.LEAN, EmberPledgePath.get(d));

        EmberPledgePath.apply(d, EmberPledgePath.BOTH, pool);
        assertTrue(EmberPledgeService.isOn(d, "lean"));
        assertTrue(EmberPledgeService.isOn(d, "reverse"));

        EmberPledgePath.apply(d, EmberPledgePath.NONE, pool);
        assertFalse(EmberPledgeService.isOn(d, "lean"));
        assertFalse(EmberPledgeService.isOn(d, "reverse"));
        assertEquals(EmberPledgePath.NONE, EmberPledgePath.get(d));
    }
}
