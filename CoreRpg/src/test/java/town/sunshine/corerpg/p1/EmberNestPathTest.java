package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberNestPathTest {
    @Test public void policies() {
        assertTrue(EmberNestPath.shouldAuto(EmberNestPath.AUTO));
        assertTrue(EmberNestPath.shouldMute(EmberNestPath.MUTE));
        assertEquals(EmberNestPath.ASK, EmberNestPath.parse("提醒"));
        assertEquals("auto", EmberNestPath.key(EmberNestPath.AUTO));
    }
}
