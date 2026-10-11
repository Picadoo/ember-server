package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberCrystalExtraTest {
    @Test public void once() {
        EmberCrystalExtra.clearAll();
        assertEquals(1, EmberCrystalExtra.consumeExtra("r1"));
        assertEquals(0, EmberCrystalExtra.consumeExtra("r1"));
    }
}
