package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemHoleUnlockSoftTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemHoleUnlockSoft.ready(d));
        d.addPeriodCount(EmberGemHoleUnlockSoft.C_USED, "day", 1);
        assertTrue(EmberGemHoleUnlockSoft.usedToday(d));
        assertFalse(EmberGemHoleUnlockSoft.ready(d));
    }
}
