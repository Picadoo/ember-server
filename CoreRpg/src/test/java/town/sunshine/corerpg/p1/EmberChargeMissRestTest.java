package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberChargeMissRestTest {
    @Test
    public void refundOncePerDay() {
        PlayerData d = new PlayerData();
        assertTrue(EmberChargeMissRest.ready(d));
        long ms = EmberChargeMissRest.consumeRefundMs(d, 8);
        assertEquals(4000L, ms);
        assertTrue(EmberChargeMissRest.usedToday(d));
        assertEquals(0L, EmberChargeMissRest.consumeRefundMs(d, 8));
    }
}
