package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapOriginWearBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapOriginWearBone.ready(d));
        d.addPeriodCount(EmberMapOriginWearBone.C_USED, "day", 1);
        assertFalse(EmberMapOriginWearBone.ready(d));
    }
}
