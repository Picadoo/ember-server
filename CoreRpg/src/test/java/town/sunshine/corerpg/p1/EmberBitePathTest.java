package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.LifeService;
public class EmberBitePathTest {
    @Test public void policies() {
        assertTrue(EmberBitePath.shouldAuto(EmberBitePath.AUTO));
        assertTrue(EmberBitePath.shouldMute(EmberBitePath.MUTE));
        assertEquals(EmberBitePath.ASK, EmberBitePath.parse("提醒"));
        assertTrue(LifeService.BITE_HUNGER_NEED < 20);
    }
    @Test public void tipFtOfferPickConstants() {
        assertEquals("/ember_p1_bite_path", EmberBitePath.PATH_PICK_CMD);
        assertEquals("[果腹路径]", EmberBitePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBitePath.PATH_ADVENTURE_CMD);
        assertEquals("果腹路径 · 果腹·自动", EmberBitePath.menuLine(EmberBitePath.AUTO));
        String g = EmberBitePath.glance(EmberBitePath.NONE, true);
        assertTrue(g.contains("点果腹路径页"));
    }
}
