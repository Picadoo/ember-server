package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberPetFeedWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberPetFeedWinBone.ready(d));
        d.addPeriodCount(EmberPetFeedWinBone.C_USED, "day", 1);
        assertFalse(EmberPetFeedWinBone.ready(d));
    }
}
