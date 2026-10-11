package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberCovenantSetWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberCovenantSetWinBone.ready(d));
        d.addPeriodCount(EmberCovenantSetWinBone.C_USED, "day", 1);
        assertFalse(EmberCovenantSetWinBone.ready(d));
    }
}
