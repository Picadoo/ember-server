package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRodBuySoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(50, EmberRodBuySoft.consumeCoin(d, 100));
        assertEquals(100, EmberRodBuySoft.consumeCoin(d, 100));
    }
}
