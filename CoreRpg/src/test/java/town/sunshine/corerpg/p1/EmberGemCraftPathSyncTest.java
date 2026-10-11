package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemCraftPathSyncTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemCraftPathSync.ready(d));
        d.addPeriodCount(EmberGemCraftPathSync.C_USED, "day", 1);
        assertFalse(EmberGemCraftPathSync.ready(d));
    }
}
