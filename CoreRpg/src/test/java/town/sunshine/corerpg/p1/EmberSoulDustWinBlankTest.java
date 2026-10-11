package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberSoulDustWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberSoulDustWinBlank.ready(d));
        d.addPeriodCount(EmberSoulDustWinBlank.C_USED, "day", 1);
        assertFalse(EmberSoulDustWinBlank.ready(d));
    }
}
