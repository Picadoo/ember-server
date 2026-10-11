package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBrewCoinSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(50, EmberBrewCoinSoft.consumeCoin(d, 100));
        assertEquals(100, EmberBrewCoinSoft.consumeCoin(d, 100));
    }
}
