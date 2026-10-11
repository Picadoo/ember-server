package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberEnhanceFailBoneTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberEnhanceFailBone.ready(d));
        d.addPeriodCount(EmberEnhanceFailBone.C_USED, "day", 1);
        assertFalse(EmberEnhanceFailBone.ready(d));
    }
}
