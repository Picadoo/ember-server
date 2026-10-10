package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberGatePathTest {
    @Test public void policies() {
        assertTrue(EmberGatePath.shouldAuto(EmberGatePath.AUTO));
        assertTrue(EmberGatePath.shouldMute(EmberGatePath.MUTE));
        assertEquals(EmberGatePath.ASK, EmberGatePath.parse("提醒"));
        assertEquals("auto", EmberGatePath.key(EmberGatePath.AUTO));
    }
}
