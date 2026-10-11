package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberBountyWeightBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(2, EmberBountyWeightBoost.apply(d, 1, false));
        assertEquals(1, EmberBountyWeightBoost.apply(d, 1, false));
        assertEquals(2, EmberBountyWeightBoost.apply(new PlayerData(), 2, true)); // raid no boost
    }
}
