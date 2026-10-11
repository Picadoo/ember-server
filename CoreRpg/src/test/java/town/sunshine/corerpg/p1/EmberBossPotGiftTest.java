package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBossPotGiftTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberBossPotGift.ready(d));
        d.addPeriodCount(EmberBossPotGift.C_USED, "day", 1);
        assertFalse(EmberBossPotGift.ready(d));
    }
}
