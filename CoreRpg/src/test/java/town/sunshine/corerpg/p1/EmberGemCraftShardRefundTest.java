package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemCraftShardRefundTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemCraftShardRefund.ready(d));
        d.addPeriodCount(EmberGemCraftShardRefund.C_USED, "day", 1);
        assertFalse(EmberGemCraftShardRefund.ready(d));
    }
}
