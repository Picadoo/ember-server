package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberArmorPathTest {
    @Test public void policies() {
        assertTrue(EmberArmorPath.shouldAuto(EmberArmorPath.AUTO));
        assertTrue(EmberArmorPath.shouldMute(EmberArmorPath.MUTE));
        assertEquals(EmberArmorPath.ASK, EmberArmorPath.parse("提醒"));
    }
}
