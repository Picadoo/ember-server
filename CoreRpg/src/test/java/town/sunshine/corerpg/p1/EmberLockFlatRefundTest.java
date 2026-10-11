package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLockFlatRefundTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLockFlatRefund.ready(d));
        d.addPeriodCount(EmberLockFlatRefund.C_USED, "day", 1);
        assertFalse(EmberLockFlatRefund.ready(d));
    }
}
