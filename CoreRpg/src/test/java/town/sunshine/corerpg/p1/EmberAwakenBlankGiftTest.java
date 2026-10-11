package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAwakenBlankGiftTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAwakenBlankGift.ready(d));
        d.addPeriodCount(EmberAwakenBlankGift.C_USED, "day", 1);
        assertFalse(EmberAwakenBlankGift.ready(d));
    }
}
