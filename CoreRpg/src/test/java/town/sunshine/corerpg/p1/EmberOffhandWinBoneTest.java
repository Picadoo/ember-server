package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberOffhandWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberOffhandWinBone.ready(d));
        d.addPeriodCount(EmberOffhandWinBone.C_USED, "day", 1);
        assertFalse(EmberOffhandWinBone.ready(d));
    }
}
