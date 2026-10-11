package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBreadWinBoneTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberBreadWinBone.ready(d));
        d.addPeriodCount(EmberBreadWinBone.C_USED, "day", 1);
        assertFalse(EmberBreadWinBone.ready(d));
    }
}
