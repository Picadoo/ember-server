package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapFarmHitBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapFarmHitBone.ready(d));
        d.addPeriodCount(EmberMapFarmHitBone.C_USED, "day", 1);
        assertFalse(EmberMapFarmHitBone.ready(d));
    }
}
