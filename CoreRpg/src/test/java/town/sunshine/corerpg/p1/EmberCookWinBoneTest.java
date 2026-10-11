package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberCookWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberCookWinBone.ready(d));
        d.addPeriodCount(EmberCookWinBone.C_USED, "day", 1);
        assertFalse(EmberCookWinBone.ready(d));
    }
}
