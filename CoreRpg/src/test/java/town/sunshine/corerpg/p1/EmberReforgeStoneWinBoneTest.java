package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberReforgeStoneWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberReforgeStoneWinBone.ready(d));
        d.addPeriodCount(EmberReforgeStoneWinBone.C_USED, "day", 1);
        assertFalse(EmberReforgeStoneWinBone.ready(d));
    }
}
