package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberArmorPathTest {
    @Test public void policies() {
        assertTrue(EmberArmorPath.shouldAuto(EmberArmorPath.AUTO));
        assertTrue(EmberArmorPath.shouldMute(EmberArmorPath.MUTE));
        assertEquals(EmberArmorPath.ASK, EmberArmorPath.parse("提醒"));
    }
    @Test public void tipFoOfferPickConstants() {
        assertEquals("/ember_p1_armor_path", EmberArmorPath.PATH_PICK_CMD);
        assertEquals("[护甲路径]", EmberArmorPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberArmorPath.PATH_ADVENTURE_CMD);
        assertEquals("护甲路径 · 护甲·自动", EmberArmorPath.menuLine(EmberArmorPath.AUTO));
        String g = EmberArmorPath.glance(EmberArmorPath.NONE, true);
        assertTrue(g.contains("点护甲路径页"));
    }
}
