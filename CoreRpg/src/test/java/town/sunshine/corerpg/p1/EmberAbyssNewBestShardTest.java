package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAbyssNewBestShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAbyssNewBestShard.ready(d));
        d.addPeriodCount(EmberAbyssNewBestShard.C_USED, "day", 1);
        assertFalse(EmberAbyssNewBestShard.ready(d));
    }
}
