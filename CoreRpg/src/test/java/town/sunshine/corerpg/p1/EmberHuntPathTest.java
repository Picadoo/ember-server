package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberHuntPathTest {
    @Test public void policies() {
        assertTrue(EmberHuntPath.shouldAuto(EmberHuntPath.AUTO));
        assertTrue(EmberHuntPath.shouldMute(EmberHuntPath.MUTE));
        assertEquals(EmberHuntPath.ASK, EmberHuntPath.parse("提醒"));
        assertEquals(EmberRunRules.Extra.TREASURE, EmberExtraPath.toExtra(EmberExtraPath.TREASURE));
        assertEquals("auto", EmberHuntPath.key(EmberHuntPath.AUTO));
    }
    @Test public void tipFeOfferPickConstants() {
        assertEquals("/ember_p1_hunt_path", EmberHuntPath.PATH_PICK_CMD);
        assertEquals("[猎侧路径]", EmberHuntPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberHuntPath.PATH_ADVENTURE_CMD);
        assertEquals("猎侧路径 · 猎侧·自动", EmberHuntPath.menuLine(EmberHuntPath.AUTO));
        String g = EmberHuntPath.glance(EmberHuntPath.NONE, true);
        assertTrue(g.contains("点猎侧路径页"));
        assertFalse(g.contains("/corerpg p1 huntpath"));
    }
}
