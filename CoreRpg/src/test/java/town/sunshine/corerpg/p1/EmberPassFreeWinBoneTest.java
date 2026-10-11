package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberPassFreeWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberPassFreeWinBone.ready(d));
        d.addPeriodCount(EmberPassFreeWinBone.C_USED, "day", 1);
        assertFalse(EmberPassFreeWinBone.ready(d));
    }
}
