package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
import town.sunshine.corerpg.LifeService;
public class EmberBrewPathTest {
    @Test public void policies() {
        assertTrue(EmberBrewPath.shouldAuto(EmberBrewPath.AUTO));
        assertTrue(EmberBrewPath.shouldMute(EmberBrewPath.MUTE));
        assertEquals(EmberBrewPath.ASK, EmberBrewPath.parse("提醒"));
        assertEquals("heal_potion", LifeService.BREW_OFFER_ID);
    }
    @Test public void tipFyOfferPickConstants() {
        assertEquals("/ember_p1_brew_path", EmberBrewPath.PATH_PICK_CMD);
        assertEquals("[熬药路径]", EmberBrewPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBrewPath.PATH_ADVENTURE_CMD);
        assertEquals("熬药路径 · 熬药·自动", EmberBrewPath.menuLine(EmberBrewPath.AUTO));
        String g = EmberBrewPath.glance(EmberBrewPath.NONE, true);
        assertTrue(g.contains("点熬药路径页"));
    }
}
