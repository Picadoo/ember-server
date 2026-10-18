package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberChestPathTest {
    @Test public void policies() {
        assertTrue(EmberChestPath.shouldAuto(EmberChestPath.AUTO));
        assertTrue(EmberChestPath.shouldMute(EmberChestPath.MUTE));
        assertEquals(EmberChestPath.ASK, EmberChestPath.parse("提醒"));
        assertEquals(EmberRunRules.Extra.CHEST, EmberExtraPath.toExtra(EmberExtraPath.CHEST));
    }
    @Test public void tipFjOfferPickConstants() {
        assertEquals("/ember_p1_chest_path", EmberChestPath.PATH_PICK_CMD);
        assertEquals("[开箱路径]", EmberChestPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberChestPath.PATH_ADVENTURE_CMD);
        assertEquals("开箱路径 · 开箱·自动", EmberChestPath.menuLine(EmberChestPath.AUTO));
        String g = EmberChestPath.glance(EmberChestPath.NONE, true);
        assertTrue(g.contains("点开箱路径页"));
        assertFalse(g.contains("/corerpg p1 chestpath"));
    }
}
