package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMonthlyLoginWinShardTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMonthlyLoginWinShard.ready(d));
        d.addPeriodCount(EmberMonthlyLoginWinShard.C_USED, "day", 1);
        assertFalse(EmberMonthlyLoginWinShard.ready(d));
    }
}
