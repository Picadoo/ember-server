package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGuildPathTest {
    @Test public void policies() {
        assertTrue(EmberGuildPath.shouldAutoAccept(EmberGuildPath.OPEN));
        assertFalse(EmberGuildPath.shouldAutoAccept(EmberGuildPath.GATE));
        assertTrue(EmberGuildPath.shouldAutoDeny(EmberGuildPath.BUSY));
        assertEquals(EmberGuildPath.OPEN, EmberGuildPath.parse("敞开"));
    }
}
