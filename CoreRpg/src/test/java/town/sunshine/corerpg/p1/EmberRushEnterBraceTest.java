package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRushEnterBraceTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberRushEnterBrace.ready(d));
        d.addPeriodCount(EmberRushEnterBrace.C_USED, "day", 1);
        assertFalse(EmberRushEnterBrace.ready(d));
    }
}
