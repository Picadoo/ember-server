package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFailRefundFlatBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(20, EmberFailRefundFlatBoost.apply(d, 15));
        assertEquals(15, EmberFailRefundFlatBoost.apply(d, 15));
        assertEquals(0, EmberFailRefundFlatBoost.apply(new PlayerData(), 0));
    }
}
