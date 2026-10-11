package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberChallengeEnterBraceTest {
    @Test public void latch() {
        PlayerData d = new PlayerData();
        assertTrue(EmberChallengeEnterBrace.ready(d));
        d.addPeriodCount(EmberChallengeEnterBrace.C_USED, "day", 1);
        assertFalse(EmberChallengeEnterBrace.ready(d));
    }
}
