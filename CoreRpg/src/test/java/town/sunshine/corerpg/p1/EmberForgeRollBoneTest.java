package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberForgeRollBoneTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberForgeRollBone.ready(d));
        d.addPeriodCount(EmberForgeRollBone.C_USED, "day", 1);
        assertFalse(EmberForgeRollBone.ready(d));
    }
}
