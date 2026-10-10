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
}
