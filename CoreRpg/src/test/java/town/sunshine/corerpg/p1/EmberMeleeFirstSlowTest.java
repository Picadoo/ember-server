package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMeleeFirstSlowTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMeleeFirstSlow.ready(d));
        d.addPeriodCount(EmberMeleeFirstSlow.C_USED, "day", 1);
        assertFalse(EmberMeleeFirstSlow.ready(d));
    }
}
