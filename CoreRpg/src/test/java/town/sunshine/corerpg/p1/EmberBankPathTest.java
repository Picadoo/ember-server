package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBankPathTest {
    @Test public void policies() {
        assertTrue(EmberBankPath.shouldAuto(EmberBankPath.AUTO));
        assertTrue(EmberBankPath.shouldMute(EmberBankPath.MUTE));
        assertEquals(EmberBankPath.ASK, EmberBankPath.parse("提醒"));
    }
    @Test public void tipFsOfferPickConstants() {
        assertEquals("/ember_p1_bank_path", EmberBankPath.PATH_PICK_CMD);
        assertEquals("[取银路径]", EmberBankPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberBankPath.PATH_ADVENTURE_CMD);
        assertEquals("取银路径 · 取银·自动", EmberBankPath.menuLine(EmberBankPath.AUTO));
        String g = EmberBankPath.glance(EmberBankPath.NONE, true);
        assertTrue(g.contains("点取银路径页"));
    }
}
