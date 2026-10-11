package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBrewWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberBrewWinShard.ready(d));
        d.addPeriodCount(EmberBrewWinShard.C_USED, "day", 1);
        assertFalse(EmberBrewWinShard.ready(d));
    }
}
