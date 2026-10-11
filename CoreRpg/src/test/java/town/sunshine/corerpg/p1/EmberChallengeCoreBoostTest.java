package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberChallengeCoreBoostTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberChallengeCoreBoost.ready(d));
        d.addPeriodCount(EmberChallengeCoreBoost.C_USED, "day", 1);
        assertFalse(EmberChallengeCoreBoost.ready(d));
    }
}
