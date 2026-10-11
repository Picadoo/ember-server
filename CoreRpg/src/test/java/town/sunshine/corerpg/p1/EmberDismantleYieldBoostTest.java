package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberDismantleYieldBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(3, EmberDismantleYieldBoost.apply(d, 2, false));
        assertTrue(EmberDismantleYieldBoost.ready(d));
        assertEquals(3, EmberDismantleYieldBoost.apply(d, 2, true));
        assertEquals(2, EmberDismantleYieldBoost.apply(d, 2, true));
    }
}
