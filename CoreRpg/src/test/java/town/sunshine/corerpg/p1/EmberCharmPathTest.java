package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCharmPathTest {
    @Test public void policies() {
        assertTrue(EmberCharmPath.shouldAuto(EmberCharmPath.AUTO));
        assertTrue(EmberCharmPath.shouldMute(EmberCharmPath.MUTE));
        assertEquals(EmberCharmPath.ASK, EmberCharmPath.parse("提醒"));
    }
    @Test public void tipFmOfferPickConstants() {
        assertEquals("/ember_p1_charm_path", EmberCharmPath.PATH_PICK_CMD);
        assertEquals("[护符路径]", EmberCharmPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCharmPath.PATH_ADVENTURE_CMD);
        assertEquals("护符路径 · 护符·自动", EmberCharmPath.menuLine(EmberCharmPath.AUTO));
        String g = EmberCharmPath.glance(EmberCharmPath.NONE, true);
        assertTrue(g.contains("点护符路径页"));
        assertFalse(g.contains("/corerpg p1 charmpath"));
    }
}
