package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberHuntPathTest {
    @Test public void policies() {
        assertTrue(EmberHuntPath.shouldAuto(EmberHuntPath.AUTO));
        assertTrue(EmberHuntPath.shouldMute(EmberHuntPath.MUTE));
        assertEquals(EmberHuntPath.ASK, EmberHuntPath.parse("提醒"));
        assertEquals(EmberRunRules.Extra.TREASURE, EmberExtraPath.toExtra(EmberExtraPath.TREASURE));
    }
}
