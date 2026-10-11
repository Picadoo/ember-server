package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberEnhanceWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberEnhanceWinBone.ready(d));
        d.addPeriodCount(EmberEnhanceWinBone.C_USED, "day", 1);
        assertFalse(EmberEnhanceWinBone.ready(d));
    }
}
