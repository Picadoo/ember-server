package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemHoleSwapTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemHoleSwap.ready(d));
        d.addPeriodCount(EmberGemHoleSwap.C_USED, "day", 1);
        assertFalse(EmberGemHoleSwap.ready(d));
    }
}
