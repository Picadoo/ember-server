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
}
