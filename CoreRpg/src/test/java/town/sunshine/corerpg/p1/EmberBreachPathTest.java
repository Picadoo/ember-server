package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBreachPathTest {
    @Test public void policies() {
        assertTrue(EmberBreachPath.shouldAuto(EmberBreachPath.AUTO));
        assertTrue(EmberBreachPath.shouldMute(EmberBreachPath.MUTE));
        assertEquals(EmberBreachPath.ASK, EmberBreachPath.parse("提醒"));
        assertEquals("auto", EmberBreachPath.key(EmberBreachPath.AUTO));
    }
}
