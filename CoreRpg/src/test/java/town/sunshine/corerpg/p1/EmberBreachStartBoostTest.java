package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberBreachStartBoostTest {
    @Test public void once() {
        EmberBreachStartBoost.clearAll();
        assertEquals(1.0, EmberBreachStartBoost.consumeExtra("r1"), 1e-9);
        assertEquals(0.0, EmberBreachStartBoost.consumeExtra("r1"), 1e-9);
    }
}
