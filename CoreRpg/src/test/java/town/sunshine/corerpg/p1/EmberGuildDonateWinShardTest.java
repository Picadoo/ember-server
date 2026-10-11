package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGuildDonateWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGuildDonateWinShard.ready(d));
        d.addPeriodCount(EmberGuildDonateWinShard.C_USED, "day", 1);
        assertFalse(EmberGuildDonateWinShard.ready(d));
    }
}
