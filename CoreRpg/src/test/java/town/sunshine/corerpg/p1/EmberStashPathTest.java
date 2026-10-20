package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberStashPathTest {
    @Test public void policies() {
        assertTrue(EmberStashPath.shouldAuto(EmberStashPath.AUTO));
        assertFalse(EmberStashPath.shouldAuto(EmberStashPath.ASK));
        assertTrue(EmberStashPath.shouldMute(EmberStashPath.MUTE));
        assertEquals(EmberStashPath.AUTO, EmberStashPath.parse("自动"));
    }
    @Test public void tipFqOfferPickConstants() {
        assertEquals("/ember_p1_stash_path", EmberStashPath.PATH_PICK_CMD);
        assertEquals("[存仓路径]", EmberStashPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberStashPath.PATH_ADVENTURE_CMD);
        assertEquals("存仓路径 · 存仓·自动", EmberStashPath.menuLine(EmberStashPath.AUTO));
        String g = EmberStashPath.glance(EmberStashPath.NONE, true);
        assertTrue(g.contains("点存仓路径页"));
    }
}
