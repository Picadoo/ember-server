package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemRemovePathSyncTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemRemovePathSync.ready(d));
        d.addPeriodCount(EmberGemRemovePathSync.C_USED, "day", 1);
        assertFalse(EmberGemRemovePathSync.ready(d));
    }
}
