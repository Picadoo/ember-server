package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberArenaWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberArenaWinBlank.ready(d));
        d.addPeriodCount(EmberArenaWinBlank.C_USED, "day", 1);
        assertFalse(EmberArenaWinBlank.ready(d));
    }
}
