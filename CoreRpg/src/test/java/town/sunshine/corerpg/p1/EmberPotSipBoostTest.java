package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberPotSipBoostTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(0.25, EmberPotSipBoost.consumePct(d, 0.20), 1e-9);
        assertEquals(0.20, EmberPotSipBoost.consumePct(d, 0.20), 1e-9);
    }
}
