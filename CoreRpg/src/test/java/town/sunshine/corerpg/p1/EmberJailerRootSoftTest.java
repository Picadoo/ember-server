package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberJailerRootSoftTest {
    @Test public void once() {
        EmberJailerRootSoft.clearAll();
        assertEquals(7, EmberJailerRootSoft.consumeTicks("r1", 10));
        assertEquals(10, EmberJailerRootSoft.consumeTicks("r1", 10));
    }
}
