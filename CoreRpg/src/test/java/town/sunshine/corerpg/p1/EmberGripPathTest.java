package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGripPathTest {
    @Test public void policies() {
        assertTrue(EmberGripPath.shouldAuto(EmberGripPath.AUTO));
        assertTrue(EmberGripPath.shouldMute(EmberGripPath.MUTE));
        assertEquals(EmberGripPath.ASK, EmberGripPath.parse("提醒"));
    }
    @Test public void tipFnOfferPickConstants() {
        assertEquals("/ember_p1_grip_path", EmberGripPath.PATH_PICK_CMD);
        assertEquals("[握刃路径]", EmberGripPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberGripPath.PATH_ADVENTURE_CMD);
        assertEquals("握刃路径 · 握刃·自动", EmberGripPath.menuLine(EmberGripPath.AUTO));
        String g = EmberGripPath.glance(EmberGripPath.NONE, true);
        assertTrue(g.contains("点握刃路径页"));
    }
}
