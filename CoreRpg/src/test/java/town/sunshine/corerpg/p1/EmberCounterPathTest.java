package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCounterPathTest {
    @Test public void parse_labels_and_enter() {
        assertEquals(EmberCounterPath.WALL, EmberCounterPath.parse("wall"));
        assertEquals(EmberCounterPath.WHIFF, EmberCounterPath.parse("落空"));
        assertEquals(EmberCounterPath.BREAK, EmberCounterPath.parse("破招"));
        assertEquals(EmberCounterPath.NONE, EmberCounterPath.parse("clear"));
        assertTrue(EmberCounterPath.label(EmberCounterPath.WALL).contains("撞墙"));
        assertTrue(EmberCounterPath.enterCmd(EmberCounterPath.BREAK).contains("q06"));
        assertTrue(EmberCounterPath.glance(EmberCounterPath.WHIFF).contains("落空"));
    }
}
