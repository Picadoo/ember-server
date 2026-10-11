package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRaidCoinBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberRaidCoinBoost.consume(d));
        assertFalse(EmberRaidCoinBoost.consume(d));
    }
}
