package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRaidEnterBraceTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberRaidEnterBrace.ready(d));
        d.addPeriodCount(EmberRaidEnterBrace.C_USED, "day", 1);
        assertFalse(EmberRaidEnterBrace.ready(d));
    }
}
