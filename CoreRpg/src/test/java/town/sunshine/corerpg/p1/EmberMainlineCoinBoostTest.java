package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMainlineCoinBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMainlineCoinBoost.consume(d));
        assertFalse(EmberMainlineCoinBoost.consume(d));
    }
}
