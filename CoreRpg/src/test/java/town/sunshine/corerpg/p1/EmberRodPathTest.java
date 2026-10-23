package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.LifeService;
public class EmberRodPathTest {
    @Test public void policies() {
        assertTrue(EmberRodPath.shouldAuto(EmberRodPath.AUTO));
        assertTrue(EmberRodPath.shouldMute(EmberRodPath.MUTE));
        assertEquals(EmberRodPath.ASK, EmberRodPath.parse("提醒"));
        assertEquals("rod", LifeService.ROD_OFFER_ID);
    }

    @Test public void tipGaOfferPickConstants() {
        assertEquals("/ember_p1_rod_path", EmberRodPath.PATH_PICK_CMD);
        assertEquals("[钓竿路径]", EmberRodPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberRodPath.PATH_ADVENTURE_CMD);
        assertEquals("钓竿路径 · 钓竿·自动", EmberRodPath.menuLine(EmberRodPath.AUTO));
        String g = EmberRodPath.glance(EmberRodPath.NONE, true);
        assertTrue(g.contains("点钓竿路径页"));
    }
}
