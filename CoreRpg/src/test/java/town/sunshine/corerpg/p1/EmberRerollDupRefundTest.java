package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRerollDupRefundTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberRerollDupRefund.consume(d));
        assertFalse(EmberRerollDupRefund.consume(d));
    }
}
