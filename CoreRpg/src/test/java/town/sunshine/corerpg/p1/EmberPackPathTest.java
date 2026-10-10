package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPackPathTest {
    @Test public void policies() {
        assertTrue(EmberPackPath.shouldAuto(EmberPackPath.AUTO));
        assertTrue(EmberPackPath.shouldMute(EmberPackPath.MUTE));
        assertEquals(EmberPackPath.ASK, EmberPackPath.parse("提醒"));
        assertEquals(1, EmberPackPath.TIGHT_SLOTS);
    }
}
