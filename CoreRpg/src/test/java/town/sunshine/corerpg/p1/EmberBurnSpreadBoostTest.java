package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBurnSpreadBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1000L, EmberBurnSpreadBoost.consume(d));
        assertEquals(0L, EmberBurnSpreadBoost.consume(d));
    }
}
