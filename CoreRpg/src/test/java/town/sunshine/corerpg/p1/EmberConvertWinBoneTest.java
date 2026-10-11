package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberConvertWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberConvertWinBone.ready(d));
        d.addPeriodCount(EmberConvertWinBone.C_USED, "day", 1);
        assertFalse(EmberConvertWinBone.ready(d));
    }
}
