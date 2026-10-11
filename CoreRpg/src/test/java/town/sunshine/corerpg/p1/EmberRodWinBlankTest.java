package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRodWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberRodWinBlank.ready(d));
        d.addPeriodCount(EmberRodWinBlank.C_USED, "day", 1);
        assertFalse(EmberRodWinBlank.ready(d));
    }
}
