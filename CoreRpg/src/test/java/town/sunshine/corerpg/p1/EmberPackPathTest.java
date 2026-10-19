package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPackPathTest {
    @Test public void policies() {
        assertTrue(EmberPackPath.shouldAuto(EmberPackPath.AUTO));
        assertTrue(EmberPackPath.shouldMute(EmberPackPath.MUTE));
        assertEquals(EmberPackPath.ASK, EmberPackPath.parse("提醒"));
        assertEquals(1, EmberPackPath.TIGHT_SLOTS);
    }
    @Test public void tipFkOfferPickConstants() {
        assertEquals("/ember_p1_pack_path", EmberPackPath.PATH_PICK_CMD);
        assertEquals("[腾包路径]", EmberPackPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberPackPath.PATH_ADVENTURE_CMD);
        assertEquals("腾包路径 · 腾包·自动", EmberPackPath.menuLine(EmberPackPath.AUTO));
        String g = EmberPackPath.glance(EmberPackPath.NONE, true);
        assertTrue(g.contains("点腾包路径页"));
        assertFalse(g.contains("/corerpg p1 packpath"));
    }
}
