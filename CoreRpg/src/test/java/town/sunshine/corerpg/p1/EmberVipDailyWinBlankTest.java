package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberVipDailyWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberVipDailyWinBlank.ready(d));
        d.addPeriodCount(EmberVipDailyWinBlank.C_USED, "day", 1);
        assertFalse(EmberVipDailyWinBlank.ready(d));
    }
}
