package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberRelayForgiveTest {
    @Test public void once() {
        EmberRelayForgive.clearAll();
        assertTrue(EmberRelayForgive.consume("r1"));
        assertFalse(EmberRelayForgive.consume("r1"));
    }
}
