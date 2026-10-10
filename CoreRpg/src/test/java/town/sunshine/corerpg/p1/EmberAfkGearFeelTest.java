package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.PlayerData;
public class EmberAfkGearFeelTest {
    @Test public void bandStableGate() {
        assertEquals("够稳", EmberAfkGearEff.band(100, 300, 1));
        assertEquals("偏弱", EmberAfkGearEff.band(1, 1, 4));
    }
    @Test public void latchKey() {
        assertEquals("p1_afk_gear_stable_t", EmberAfkGearFeel.C_STABLE);
    }
    @Test public void nullPlayerNoCue() {
        PlayerData d = new PlayerData();
        assertFalse(EmberAfkGearFeel.maybeStableCue(null, d, 1, 100, 300));
    }
}
