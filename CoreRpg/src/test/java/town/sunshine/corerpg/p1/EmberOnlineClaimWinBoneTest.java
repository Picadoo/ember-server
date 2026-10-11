package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberOnlineClaimWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberOnlineClaimWinBone.ready(d));
        d.addPeriodCount(EmberOnlineClaimWinBone.C_USED, "day", 1);
        assertFalse(EmberOnlineClaimWinBone.ready(d));
    }
}
