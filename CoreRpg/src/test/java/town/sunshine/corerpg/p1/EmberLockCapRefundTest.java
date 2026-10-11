package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLockCapRefundTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLockCapRefund.ready(d));
        d.addPeriodCount(EmberLockCapRefund.C_USED, "day", 1);
        assertFalse(EmberLockCapRefund.ready(d));
    }
}
