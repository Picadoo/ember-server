package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberRoomClearHealTest {
    @Test public void latch() {
        EmberRoomClearHeal.clearAll();
        assertFalse(EmberRoomClearHeal.used("r1"));
        EmberRoomClearHeal.clear("r1");
        assertEquals(0.025, EmberRoomClearHeal.HEAL_PCT, 1e-9);
    }
}
