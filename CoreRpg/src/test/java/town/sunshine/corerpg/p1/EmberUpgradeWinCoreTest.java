package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberUpgradeWinCoreTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberUpgradeWinCore.ready(d));
        d.addPeriodCount(EmberUpgradeWinCore.C_USED, "day", 1);
        assertFalse(EmberUpgradeWinCore.ready(d));
    }
}
