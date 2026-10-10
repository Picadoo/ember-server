package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDuckPathTest {
    @Test public void policies() {
        assertTrue(EmberDuckPath.shouldAuto(EmberDuckPath.AUTO));
        assertTrue(EmberDuckPath.shouldMute(EmberDuckPath.MUTE));
        assertEquals(EmberDuckPath.ASK, EmberDuckPath.parse("提醒"));
        assertEquals("auto", EmberDuckPath.key(EmberDuckPath.AUTO));
    }
}
