package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemInsertShardTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemInsertShard.ready(d));
        d.addPeriodCount(EmberGemInsertShard.C_USED, "day", 1);
        assertFalse(EmberGemInsertShard.ready(d));
    }
}
