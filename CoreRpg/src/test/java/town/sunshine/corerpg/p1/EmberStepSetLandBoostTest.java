package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberStepSetLandBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertTrue(EmberStepSetLandBoost.consume(d));
        assertFalse(EmberStepSetLandBoost.consume(d));
    }
}
