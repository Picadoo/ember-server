package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGearUnstashWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGearUnstashWinBlank.ready(d));
        d.addPeriodCount(EmberGearUnstashWinBlank.C_USED, "day", 1);
        assertFalse(EmberGearUnstashWinBlank.ready(d));
    }
}
