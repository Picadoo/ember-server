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
}
