package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMainlineEnterBraceTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMainlineEnterBrace.ready(d));
        d.addPeriodCount(EmberMainlineEnterBrace.C_USED, "day", 1);
        assertFalse(EmberMainlineEnterBrace.ready(d));
    }
}
