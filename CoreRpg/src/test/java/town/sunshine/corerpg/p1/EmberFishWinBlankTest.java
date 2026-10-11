package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFishWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberFishWinBlank.ready(d));
        d.addPeriodCount(EmberFishWinBlank.C_USED, "day", 1);
        assertFalse(EmberFishWinBlank.ready(d));
    }
}
