package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSoulDustCoinSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSoulDustCoinSoft.isSoulDustOffer("soul_dust"));
        assertEquals(2, EmberSoulDustCoinSoft.consumeCoin(d, 5));
        assertEquals(5, EmberSoulDustCoinSoft.consumeCoin(d, 5));
    }
}
