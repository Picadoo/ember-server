package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapCodexShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapCodexShard.ready(d));
        d.addPeriodCount(EmberMapCodexShard.C_USED, "day", 1);
        assertFalse(EmberMapCodexShard.ready(d));
    }
}
