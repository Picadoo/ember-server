package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberLinkPathTest {
    @Test public void policies() {
        assertTrue(EmberLinkPath.shouldAuto(EmberLinkPath.AUTO));
        assertTrue(EmberLinkPath.shouldMute(EmberLinkPath.MUTE));
        assertEquals(EmberLinkPath.ASK, EmberLinkPath.parse("提醒"));
        assertEquals("auto", EmberLinkPath.key(EmberLinkPath.AUTO));
    }
    @Test public void tipFfOfferPickConstants() {
        assertEquals("/ember_p1_link_path", EmberLinkPath.PATH_PICK_CMD);
        assertEquals("[跃迁路径]", EmberLinkPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberLinkPath.PATH_ADVENTURE_CMD);
        assertEquals("跃迁路径 · 跃迁·自动", EmberLinkPath.menuLine(EmberLinkPath.AUTO));
        String g = EmberLinkPath.glance(EmberLinkPath.NONE, true);
        assertTrue(g.contains("点跃迁路径页"));
        assertFalse(g.contains("/corerpg p1 linkpath"));
    }
}
