package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBankPathTest {
    @Test public void policies() {
        assertTrue(EmberBankPath.shouldAuto(EmberBankPath.AUTO));
        assertTrue(EmberBankPath.shouldMute(EmberBankPath.MUTE));
        assertEquals(EmberBankPath.ASK, EmberBankPath.parse("提醒"));
    }
}
