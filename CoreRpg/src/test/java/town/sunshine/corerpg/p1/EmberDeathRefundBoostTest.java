package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberDeathRefundBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(7, EmberDeathRefundBoost.applyMax(d, 5));
        assertEquals(5, EmberDeathRefundBoost.applyMax(d, 5));
    }
}
