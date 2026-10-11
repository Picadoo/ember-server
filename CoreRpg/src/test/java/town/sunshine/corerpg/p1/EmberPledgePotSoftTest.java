package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberPledgePotSoftTest {
    @Test public void once() {
        EmberPledgePotSoft.clearAll();
        assertTrue(EmberPledgePotSoft.consumeForgive("r1"));
        assertFalse(EmberPledgePotSoft.consumeForgive("r1"));
    }
}
