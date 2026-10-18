package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFleePathTest {
    @Test public void policies() {
        assertTrue(EmberFleePath.shouldAuto(EmberFleePath.AUTO));
        assertTrue(EmberFleePath.shouldMute(EmberFleePath.MUTE));
        assertEquals(EmberFleePath.ASK, EmberFleePath.parse("提醒"));
        assertEquals("auto", EmberFleePath.key(EmberFleePath.AUTO));
    }
    @Test public void tipFiOfferPickConstants() {
        assertEquals("/ember_p1_flee_path", EmberFleePath.PATH_PICK_CMD);
        assertEquals("[撤离路径]", EmberFleePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberFleePath.PATH_ADVENTURE_CMD);
        assertEquals("撤离路径 · 撤离·自动", EmberFleePath.menuLine(EmberFleePath.AUTO));
        String g = EmberFleePath.glance(EmberFleePath.NONE, true);
        assertTrue(g.contains("点撤离路径页"));
        assertFalse(g.contains("/corerpg p1 flepath"));
    }
}
