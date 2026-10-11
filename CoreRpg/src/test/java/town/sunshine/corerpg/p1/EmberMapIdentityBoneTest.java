package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapIdentityBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapIdentityBone.ready(d));
        d.addPeriodCount(EmberMapIdentityBone.C_USED, "day", 1);
        assertFalse(EmberMapIdentityBone.ready(d));
    }
}
