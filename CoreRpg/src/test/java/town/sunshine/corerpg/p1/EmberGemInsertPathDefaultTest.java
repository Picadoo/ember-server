package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberGemInsertPathDefaultTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberGemInsertPathDefault.ready(d));
        d.addPeriodCount(EmberGemInsertPathDefault.C_USED, "day", 1);
        assertFalse(EmberGemInsertPathDefault.ready(d));
    }
}
