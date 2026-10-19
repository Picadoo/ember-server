package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSightPathTest {
    @Test public void policies() {
        assertTrue(EmberSightPath.shouldAuto(EmberSightPath.AUTO));
        assertTrue(EmberSightPath.shouldMute(EmberSightPath.MUTE));
        assertEquals(EmberSightPath.ASK, EmberSightPath.parse("提醒"));
        assertEquals(20 * 60 * 5, EmberSightPath.NV_TICKS);
    }
    @Test public void tipFlOfferPickConstants() {
        assertEquals("/ember_p1_sight_path", EmberSightPath.PATH_PICK_CMD);
        assertEquals("[夜视路径]", EmberSightPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSightPath.PATH_ADVENTURE_CMD);
        assertEquals("夜视路径 · 夜视·自动", EmberSightPath.menuLine(EmberSightPath.AUTO));
        String g = EmberSightPath.glance(EmberSightPath.NONE, true);
        assertTrue(g.contains("点夜视路径页"));
        assertFalse(g.contains("/corerpg p1 sightpath"));
    }
}
