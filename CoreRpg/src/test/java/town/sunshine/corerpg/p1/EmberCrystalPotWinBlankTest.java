package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberCrystalPotWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberCrystalPotWinBlank.ready(d));
        d.addPeriodCount(EmberCrystalPotWinBlank.C_USED, "day", 1);
        assertFalse(EmberCrystalPotWinBlank.ready(d));
    }
}
