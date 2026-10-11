package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberRushSigBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(2, EmberRushSigBoost.apply(d, 1));
        assertEquals(1, EmberRushSigBoost.apply(d, 1));
    }
}
