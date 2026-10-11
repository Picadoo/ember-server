package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAnvilRepairWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAnvilRepairWinShard.ready(d));
        d.addPeriodCount(EmberAnvilRepairWinShard.C_USED, "day", 1);
        assertFalse(EmberAnvilRepairWinShard.ready(d));
    }
}
