package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRefineWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberRefineWinBone.ready(d));
        d.addPeriodCount(EmberRefineWinBone.C_USED, "day", 1);
        assertFalse(EmberRefineWinBone.ready(d));
    }
}
