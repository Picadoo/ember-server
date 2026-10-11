package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberHoldRadiusBoostTest {
    @Test public void once() {
        EmberHoldRadiusBoost.clearAll();
        assertEquals(0.5, EmberHoldRadiusBoost.consumeExtra("r1"), 1e-9);
        assertEquals(0.0, EmberHoldRadiusBoost.consumeExtra("r1"), 1e-9);
    }
}
