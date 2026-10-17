package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGatePathTest {
    @Test public void policies() {
        assertTrue(EmberGatePath.shouldAuto(EmberGatePath.AUTO));
        assertTrue(EmberGatePath.shouldMute(EmberGatePath.MUTE));
        assertEquals(EmberGatePath.ASK, EmberGatePath.parse("提醒"));
        assertEquals("auto", EmberGatePath.key(EmberGatePath.AUTO));
    }
    @Test public void tipFhOfferPickConstants() {
        assertEquals("/ember_p1_gate_path", EmberGatePath.PATH_PICK_CMD);
        assertEquals("[开门路径]", EmberGatePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberGatePath.PATH_ADVENTURE_CMD);
        assertEquals("开门路径 · 开门·自动", EmberGatePath.menuLine(EmberGatePath.AUTO));
        String g = EmberGatePath.glance(EmberGatePath.NONE, true);
        assertTrue(g.contains("点开门路径页"));
        assertFalse(g.contains("/corerpg p1 gatepath"));
    }
}
