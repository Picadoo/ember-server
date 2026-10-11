package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberRerollSureTest {
    @Test public void forcedAtThreshold() {
        assertTrue(EmberRerollSure.forced(5, 5));
        assertFalse(EmberRerollSure.forced(4, 5));
        assertFalse(EmberRerollSure.forced(0, 0));
    }
}
