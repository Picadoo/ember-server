package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberFriendAcceptWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberFriendAcceptWinBone.ready(d));
        d.addPeriodCount(EmberFriendAcceptWinBone.C_USED, "day", 1);
        assertFalse(EmberFriendAcceptWinBone.ready(d));
    }
}
