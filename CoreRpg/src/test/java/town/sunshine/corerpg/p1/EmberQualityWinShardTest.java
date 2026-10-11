package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberQualityWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberQualityWinShard.ready(d));
        d.addPeriodCount(EmberQualityWinShard.C_USED, "day", 1);
        assertFalse(EmberQualityWinShard.ready(d));
    }
}
