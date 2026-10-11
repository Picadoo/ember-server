package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBrandCraftWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberBrandCraftWinBlank.ready(d));
        d.addPeriodCount(EmberBrandCraftWinBlank.C_USED, "day", 1);
        assertFalse(EmberBrandCraftWinBlank.ready(d));
    }
}
