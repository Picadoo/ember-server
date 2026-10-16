package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberThawPathTest {
    @Test public void policies() {
        assertTrue(EmberThawPath.shouldAuto(EmberThawPath.AUTO));
        assertTrue(EmberThawPath.shouldMute(EmberThawPath.MUTE));
        assertEquals(EmberThawPath.ASK, EmberThawPath.parse("提醒"));
        assertEquals("auto", EmberThawPath.key(EmberThawPath.AUTO));
    }
    @Test public void tipFcMenuSurface() {
        assertEquals("/ember_p1_thaw_path", EmberThawPath.PATH_PICK_CMD);
        assertEquals("[解冻路径]", EmberThawPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberThawPath.PATH_ADVENTURE_CMD);
        assertEquals("解冻路径 · 解冻·自动", EmberThawPath.menuLine(EmberThawPath.AUTO));
        String g = EmberThawPath.glance(EmberThawPath.NONE, true);
        assertTrue(g.contains("点解冻路径页"));
        assertFalse(g.contains("/corerpg p1 thawpath"));
    }
}
