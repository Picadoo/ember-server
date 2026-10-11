package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSignWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSignWinShard.ready(d));
        d.addPeriodCount(EmberSignWinShard.C_USED, "day", 1);
        assertFalse(EmberSignWinShard.ready(d));
    }
}
