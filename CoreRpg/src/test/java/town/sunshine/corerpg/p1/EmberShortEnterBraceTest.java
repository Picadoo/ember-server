package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberShortEnterBraceTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberShortEnterBrace.ready(d));
        d.addPeriodCount(EmberShortEnterBrace.C_USED, "day", 1);
        assertFalse(EmberShortEnterBrace.ready(d));
    }
}
