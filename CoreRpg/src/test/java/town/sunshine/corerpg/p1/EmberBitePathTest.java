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
}
