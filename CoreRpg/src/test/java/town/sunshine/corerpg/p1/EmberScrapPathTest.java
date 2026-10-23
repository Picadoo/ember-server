package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberScrapPathTest {
    @Test public void policies() {
        assertTrue(EmberScrapPath.shouldAuto(EmberScrapPath.AUTO));
        assertTrue(EmberScrapPath.shouldMute(EmberScrapPath.MUTE));
        assertEquals(EmberScrapPath.ASK, EmberScrapPath.parse("提醒"));
        assertEquals("auto", EmberScrapPath.key(EmberScrapPath.AUTO));
    }

    @Test public void tipGcOfferPickConstants() {
        assertEquals("/ember_p1_scrap_path", EmberScrapPath.PATH_PICK_CMD);
        assertEquals("[分解路径]", EmberScrapPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberScrapPath.PATH_ADVENTURE_CMD);
        assertEquals("分解路径 · 分解·自动", EmberScrapPath.menuLine(EmberScrapPath.AUTO));
        String g = EmberScrapPath.glance(EmberScrapPath.NONE, true);
        assertTrue(g.contains("点分解路径页"));
    }
}
