package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDodgePathTest {
    @Test public void policies() {
        assertTrue(EmberDodgePath.shouldAuto(EmberDodgePath.AUTO));
        assertTrue(EmberDodgePath.shouldMute(EmberDodgePath.MUTE));
        assertEquals(EmberDodgePath.ASK, EmberDodgePath.parse("提醒"));
        assertEquals("auto", EmberDodgePath.key(EmberDodgePath.AUTO));
    }
}
