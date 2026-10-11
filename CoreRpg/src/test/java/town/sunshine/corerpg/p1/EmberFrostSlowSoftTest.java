package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberFrostSlowSoftTest {
    @Test public void once() {
        EmberFrostSlowSoft.clearAll();
        assertEquals(6, EmberFrostSlowSoft.consumeTicks("r1", 10));
        assertEquals(10, EmberFrostSlowSoft.consumeTicks("r1", 10));
    }
}
