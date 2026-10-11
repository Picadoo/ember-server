package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberLockTierFloorTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberLockTierFloor.ready(d));
        d.addPeriodCount(EmberLockTierFloor.C_USED, "day", 1);
        assertFalse(EmberLockTierFloor.ready(d));
    }
}
