package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberBeaconHpBoostTest {
    @Test public void once() {
        EmberBeaconHpBoost.clearAll();
        assertEquals(1.2, EmberBeaconHpBoost.consumeMult("r1"), 1e-9);
        assertEquals(1.0, EmberBeaconHpBoost.consumeMult("r1"), 1e-9);
        assertEquals(1.2, EmberBeaconHpBoost.consumeMult("r2"), 1e-9);
    }
}
