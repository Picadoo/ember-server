package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberPetHatchWinBlankTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberPetHatchWinBlank.ready(d));
        d.addPeriodCount(EmberPetHatchWinBlank.C_USED, "day", 1);
        assertFalse(EmberPetHatchWinBlank.ready(d));
    }
}
