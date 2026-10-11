package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAbyssNewBestBoneTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAbyssNewBestBone.ready(d));
        d.addPeriodCount(EmberAbyssNewBestBone.C_USED, "day", 1);
        assertFalse(EmberAbyssNewBestBone.ready(d));
    }
}
