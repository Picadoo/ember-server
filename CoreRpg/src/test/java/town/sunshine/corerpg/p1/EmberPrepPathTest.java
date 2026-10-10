package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPrepPathTest {
    @Test public void parse_targets() {
        assertEquals(EmberPrepPath.LIGHT, EmberPrepPath.parse("轻备"));
        assertEquals(EmberPrepPath.FULL, EmberPrepPath.parse("full"));
        assertEquals(EmberPrepPath.BARE, EmberPrepPath.parse("不备"));
        assertEquals(3, EmberPrepPath.targetBottles(EmberPrepPath.LIGHT));
        assertEquals(5, EmberPrepPath.targetBottles(EmberPrepPath.FULL));
        assertEquals(0, EmberPrepPath.targetBottles(EmberPrepPath.BARE));
        assertTrue(EmberPrepPath.tip(EmberPrepPath.FULL).contains("5"));
    }
}
