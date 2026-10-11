package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMapCodexClaimBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMapCodexClaimBone.ready(d));
        d.addPeriodCount(EmberMapCodexClaimBone.C_USED, "day", 1);
        assertFalse(EmberMapCodexClaimBone.ready(d));
    }
}
