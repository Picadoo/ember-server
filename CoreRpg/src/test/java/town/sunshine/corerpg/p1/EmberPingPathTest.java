package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPingPathTest {
    @Test public void policies() {
        assertTrue(EmberPingPath.shouldLivePing(EmberPingPath.OPEN));
        assertTrue(EmberPingPath.shouldDigest(EmberPingPath.GATE));
        assertTrue(EmberPingPath.shouldMute(EmberPingPath.BUSY));
        assertEquals(EmberPingPath.OPEN, EmberPingPath.parse("敞开"));
    }
}
