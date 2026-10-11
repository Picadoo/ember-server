package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberChargeLandHealTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberChargeLandHeal.ready(d));
        d.addPeriodCount(EmberChargeLandHeal.C_USED, "day", 1);
        assertFalse(EmberChargeLandHeal.ready(d));
    }
}
