package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberMentorAcceptWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberMentorAcceptWinBone.ready(d));
        d.addPeriodCount(EmberMentorAcceptWinBone.C_USED, "day", 1);
        assertFalse(EmberMentorAcceptWinBone.ready(d));
    }
}
