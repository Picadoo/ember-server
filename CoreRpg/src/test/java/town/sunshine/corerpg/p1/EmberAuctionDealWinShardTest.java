package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAuctionDealWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAuctionDealWinShard.ready(d));
        d.addPeriodCount(EmberAuctionDealWinShard.C_USED, "day", 1);
        assertFalse(EmberAuctionDealWinShard.ready(d));
    }
}
