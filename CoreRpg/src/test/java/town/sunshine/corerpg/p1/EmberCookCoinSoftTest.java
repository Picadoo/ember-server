package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberCookCoinSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberCookCoinSoft.consumeFee(d, 10));
        assertEquals(10, EmberCookCoinSoft.consumeFee(d, 10));
    }
    @Test public void peek_doesNotLatch() {
        PlayerData d = new PlayerData();
        assertEquals(5, EmberCookCoinSoft.peekFee(d, 10));
        assertTrue(EmberCookCoinSoft.ready(d));
        assertEquals(5, EmberCookCoinSoft.consumeFee(d, 10));
    }
}
