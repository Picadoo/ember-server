package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCharmPathTest {
    @Test public void policies() {
        assertTrue(EmberCharmPath.shouldAuto(EmberCharmPath.AUTO));
        assertTrue(EmberCharmPath.shouldMute(EmberCharmPath.MUTE));
        assertEquals(EmberCharmPath.ASK, EmberCharmPath.parse("提醒"));
    }
}
