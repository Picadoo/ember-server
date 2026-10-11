package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSwapWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSwapWinShard.ready(d));
        d.addPeriodCount(EmberSwapWinShard.C_USED, "day", 1);
        assertFalse(EmberSwapWinShard.ready(d));
    }
}
