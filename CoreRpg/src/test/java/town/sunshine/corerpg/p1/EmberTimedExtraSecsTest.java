package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberTimedExtraSecsTest {
    @Test public void once() {
        EmberTimedExtraSecs.clearAll();
        assertEquals(5, EmberTimedExtraSecs.consumeExtra("r1"));
        assertEquals(0, EmberTimedExtraSecs.consumeExtra("r1"));
    }
}
