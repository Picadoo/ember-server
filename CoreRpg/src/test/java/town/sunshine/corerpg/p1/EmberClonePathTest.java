package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberClonePathTest {
    @Test public void policies() {
        assertTrue(EmberClonePath.shouldAuto(EmberClonePath.AUTO));
        assertTrue(EmberClonePath.shouldMute(EmberClonePath.MUTE));
        assertEquals(EmberClonePath.ASK, EmberClonePath.parse("提醒"));
        assertEquals("auto", EmberClonePath.key(EmberClonePath.AUTO));
    }
}
