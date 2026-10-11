package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemHoleSeatPathSyncTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemHoleSeatPathSync.ready(d));
        d.addPeriodCount(EmberGemHoleSeatPathSync.C_USED, "day", 1);
        assertFalse(EmberGemHoleSeatPathSync.ready(d));
    }
}
