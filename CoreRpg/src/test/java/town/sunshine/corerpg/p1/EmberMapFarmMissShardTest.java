package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapFarmMissShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapFarmMissShard.ready(d));
        d.addPeriodCount(EmberMapFarmMissShard.C_USED, "day", 1);
        assertFalse(EmberMapFarmMissShard.ready(d));
    }
}
