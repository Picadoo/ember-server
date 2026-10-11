package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBossPotExtraTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberBossPotExtra.ready(d));
        d.addPeriodCount(EmberBossPotExtra.C_USED, "day", 1);
        assertFalse(EmberBossPotExtra.ready(d));
    }
}
