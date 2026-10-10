package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberTrialPathTest {
    @Test public void parse_map_pledge_counter() {
        assertEquals(EmberTrialPath.CAUTIOUS, EmberTrialPath.parse("稳慎"));
        assertEquals(EmberTrialPath.AGGRESSIVE, EmberTrialPath.parse("aggressive"));
        assertEquals(EmberTrialPath.PRESSURE, EmberTrialPath.parse("加压"));
        assertEquals(EmberPledgePath.LEAN, EmberTrialPath.pledgeId(EmberTrialPath.CAUTIOUS));
        assertEquals(EmberCounterPath.WALL, EmberTrialPath.counterId(EmberTrialPath.AGGRESSIVE));
        assertEquals(EmberPledgePath.BOTH, EmberTrialPath.pledgeId(EmberTrialPath.PRESSURE));
        assertEquals(EmberCounterPath.BREAK, EmberTrialPath.counterId(EmberTrialPath.PRESSURE));
        assertTrue(EmberTrialPath.tip(EmberTrialPath.CAUTIOUS).contains("限药"));
    }
}
