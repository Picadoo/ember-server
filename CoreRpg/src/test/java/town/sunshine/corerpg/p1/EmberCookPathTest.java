package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCookPathTest {
    @Test public void policies() {
        assertTrue(EmberCookPath.shouldAuto(EmberCookPath.AUTO));
        assertTrue(EmberCookPath.shouldMute(EmberCookPath.MUTE));
        assertEquals(EmberCookPath.ASK, EmberCookPath.parse("提醒"));
    }
    @Test public void tipFuOfferPickConstants() {
        assertEquals("/ember_p1_cook_path", EmberCookPath.PATH_PICK_CMD);
        assertEquals("[代烤路径]", EmberCookPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberCookPath.PATH_ADVENTURE_CMD);
        assertEquals("代烤路径 · 代烤·自动", EmberCookPath.menuLine(EmberCookPath.AUTO));
        String g = EmberCookPath.glance(EmberCookPath.NONE, true);
        assertTrue(g.contains("点代烤路径页"));
    }
}
