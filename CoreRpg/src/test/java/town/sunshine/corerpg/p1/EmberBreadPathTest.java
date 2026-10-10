package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.LifeService;
public class EmberBreadPathTest {
    @Test public void policies() {
        assertTrue(EmberBreadPath.shouldAuto(EmberBreadPath.AUTO));
        assertTrue(EmberBreadPath.shouldMute(EmberBreadPath.MUTE));
        assertEquals(EmberBreadPath.ASK, EmberBreadPath.parse("提醒"));
        assertEquals("bread", LifeService.BREAD_OFFER_ID);
    }
}
