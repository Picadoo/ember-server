package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberShortEnterSpeedTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberShortEnterSpeed.ready(d));
        d.addPeriodCount(EmberShortEnterSpeed.C_USED, "day", 1);
        assertFalse(EmberShortEnterSpeed.ready(d));
    }
}
