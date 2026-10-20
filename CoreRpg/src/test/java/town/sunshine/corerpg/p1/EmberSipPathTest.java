package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSipPathTest {
    @Test public void policies() {
        assertTrue(EmberSipPath.shouldAuto(EmberSipPath.AUTO));
        assertTrue(EmberSipPath.shouldMute(EmberSipPath.MUTE));
        assertEquals(EmberSipPath.ASK, EmberSipPath.parse("提醒"));
        assertTrue(EmberSipPath.suppressLowHpBar(null) == false);
    }
    @Test public void tipFpOfferPickConstants() {
        assertEquals("/ember_p1_sip_path", EmberSipPath.PATH_PICK_CMD);
        assertEquals("[喝药路径]", EmberSipPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberSipPath.PATH_ADVENTURE_CMD);
        assertEquals("喝药路径 · 喝药·自动", EmberSipPath.menuLine(EmberSipPath.AUTO));
        String g = EmberSipPath.glance(EmberSipPath.NONE, true);
        assertTrue(g.contains("点喝药路径页"));
    }
}
