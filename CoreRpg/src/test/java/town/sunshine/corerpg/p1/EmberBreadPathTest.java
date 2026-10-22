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

    @Test public void tipFzOfferPickConstants() {
        assertEquals("/ember_p1_bread_path", EmberBreadPath.PATH_PICK_CMD);
        assertEquals("[面包路径]", EmberBreadPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBreadPath.PATH_ADVENTURE_CMD);
        assertEquals("面包路径 · 面包·自动", EmberBreadPath.menuLine(EmberBreadPath.AUTO));
        String g = EmberBreadPath.glance(EmberBreadPath.NONE, true);
        assertTrue(g.contains("点面包路径页"));
    }
}
