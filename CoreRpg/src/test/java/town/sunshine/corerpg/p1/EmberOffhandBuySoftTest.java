package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberOffhandBuySoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberOffhandBuySoft.isOffhandOffer("offhand_ward"));
        assertEquals(40, EmberOffhandBuySoft.consumeCoin(d, 80));
        assertEquals(80, EmberOffhandBuySoft.consumeCoin(d, 80));
    }
}
