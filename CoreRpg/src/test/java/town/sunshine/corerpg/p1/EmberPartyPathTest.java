package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPartyPathTest {
    @Test public void policies() {
        assertTrue(EmberPartyPath.shouldAutoAccept(EmberPartyPath.OPEN));
        assertFalse(EmberPartyPath.shouldAutoAccept(EmberPartyPath.GATE));
        assertTrue(EmberPartyPath.shouldAutoDeny(EmberPartyPath.BUSY));
        assertEquals(EmberPartyPath.OPEN, EmberPartyPath.parse("敞开"));
    }
}
