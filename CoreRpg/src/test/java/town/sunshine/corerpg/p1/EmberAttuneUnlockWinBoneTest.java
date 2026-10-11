package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberAttuneUnlockWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberAttuneUnlockWinBone.ready(d));
        d.addPeriodCount(EmberAttuneUnlockWinBone.C_USED, "day", 1);
        assertFalse(EmberAttuneUnlockWinBone.ready(d));
    }
}
