package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAuctionSellWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAuctionSellWinShard.ready(d));
        d.addPeriodCount(EmberAuctionSellWinShard.C_USED, "day", 1);
        assertFalse(EmberAuctionSellWinShard.ready(d));
    }
}
