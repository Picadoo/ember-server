package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMarkExchangeWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMarkExchangeWinBlank.ready(d));
        d.addPeriodCount(EmberMarkExchangeWinBlank.C_USED, "day", 1);
        assertFalse(EmberMarkExchangeWinBlank.ready(d));
    }
}
