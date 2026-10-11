package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAuctionBuyWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAuctionBuyWinBlank.ready(d));
        d.addPeriodCount(EmberAuctionBuyWinBlank.C_USED, "day", 1);
        assertFalse(EmberAuctionBuyWinBlank.ready(d));
    }
}
