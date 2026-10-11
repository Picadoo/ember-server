package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemSwapSoftTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemSwapSoft.ready(d));
        d.addPeriodCount(EmberGemSwapSoft.C_USED, "day", 1);
        assertFalse(EmberGemSwapSoft.ready(d));
    }
}
