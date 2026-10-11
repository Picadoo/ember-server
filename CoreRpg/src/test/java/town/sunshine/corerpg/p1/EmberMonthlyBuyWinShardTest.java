package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMonthlyBuyWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMonthlyBuyWinShard.ready(d));
        d.addPeriodCount(EmberMonthlyBuyWinShard.C_USED, "day", 1);
        assertFalse(EmberMonthlyBuyWinShard.ready(d));
    }
}
