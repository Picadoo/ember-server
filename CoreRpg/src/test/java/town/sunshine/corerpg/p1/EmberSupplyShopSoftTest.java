package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSupplyShopSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberSupplyShopSoft.apply(d, 10, true));
        assertEquals(10, EmberSupplyShopSoft.apply(d, 10, true));
    }
    @Test public void peekPrice_readyHalves_noLatch() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberSupplyShopSoft.peekPrice(d, 10));
        assertTrue(EmberSupplyShopSoft.ready(d));
        assertEquals(5, EmberSupplyShopSoft.apply(d, 10, false));
        assertTrue(EmberSupplyShopSoft.ready(d));
    }
}
