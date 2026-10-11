package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLockMidFloorTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLockMidFloor.ready(d));
        d.addPeriodCount(EmberLockMidFloor.C_USED, "day", 1);
        assertFalse(EmberLockMidFloor.ready(d));
    }
}
