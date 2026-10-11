package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberImprintWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberImprintWinShard.ready(d));
        d.addPeriodCount(EmberImprintWinShard.C_USED, "day", 1);
        assertFalse(EmberImprintWinShard.ready(d));
    }
}
