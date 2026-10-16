package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDuckPathTest {
    @Test public void policies() {
        assertTrue(EmberDuckPath.shouldAuto(EmberDuckPath.AUTO));
        assertTrue(EmberDuckPath.shouldMute(EmberDuckPath.MUTE));
        assertEquals(EmberDuckPath.ASK, EmberDuckPath.parse("提醒"));
        assertEquals("auto", EmberDuckPath.key(EmberDuckPath.AUTO));
    }
    @Test public void tipFdMenuSurface() {
        assertEquals("/ember_p1_duck_path", EmberDuckPath.PATH_PICK_CMD);
        assertEquals("[闪爆路径]", EmberDuckPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberDuckPath.PATH_ADVENTURE_CMD);
        assertEquals("闪爆路径 · 闪爆·自动", EmberDuckPath.menuLine(EmberDuckPath.AUTO));
        String g = EmberDuckPath.glance(EmberDuckPath.NONE, true);
        assertTrue(g.contains("点闪爆路径页"));
        assertFalse(g.contains("/corerpg p1 duckpath"));
    }
}
