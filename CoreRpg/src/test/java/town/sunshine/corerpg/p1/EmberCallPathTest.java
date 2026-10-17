package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCallPathTest {
    @Test public void policies() {
        assertTrue(EmberCallPath.shouldAuto(EmberCallPath.AUTO));
        assertTrue(EmberCallPath.shouldMute(EmberCallPath.MUTE));
        assertEquals(EmberCallPath.ASK, EmberCallPath.parse("提醒"));
        assertEquals("auto", EmberCallPath.key(EmberCallPath.AUTO));
    }
    @Test public void tipFgOfferPickConstants() {
        assertEquals("/ember_p1_call_path", EmberCallPath.PATH_PICK_CMD);
        assertEquals("[召战路径]", EmberCallPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCallPath.PATH_ADVENTURE_CMD);
        assertEquals("召战路径 · 召战·自动", EmberCallPath.menuLine(EmberCallPath.AUTO));
        String g = EmberCallPath.glance(EmberCallPath.NONE, true);
        assertTrue(g.contains("点召战路径页"));
        assertFalse(g.contains("/corerpg p1 callpath"));
    }
}
