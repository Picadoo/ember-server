package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapStashPinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapStashPinBone.ready(d));
        d.addPeriodCount(EmberMapStashPinBone.C_USED, "day", 1);
        assertFalse(EmberMapStashPinBone.ready(d));
    }
}
