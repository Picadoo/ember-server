package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberChallengeShardBoostTest {
    @Test public void readyOnce() {
        PlayerData d = new PlayerData();
        assertTrue(EmberChallengeShardBoost.ready(d));
        d.addPeriodCount(EmberChallengeShardBoost.C_USED, "day", 1);
        assertFalse(EmberChallengeShardBoost.ready(d));
    }
}
