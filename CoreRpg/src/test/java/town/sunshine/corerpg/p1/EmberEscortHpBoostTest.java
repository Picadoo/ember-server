package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberEscortHpBoostTest {
    @Test public void once() {
        EmberEscortHpBoost.clearAll();
        assertEquals(1.2, EmberEscortHpBoost.consumeMult("r1"), 1e-9);
        assertEquals(1.0, EmberEscortHpBoost.consumeMult("r1"), 1e-9);
    }
}
