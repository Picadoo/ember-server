package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStableCharmWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberStableCharmWinShard.ready(d));
        d.addPeriodCount(EmberStableCharmWinShard.C_USED, "day", 1);
        assertFalse(EmberStableCharmWinShard.ready(d));
    }
}
