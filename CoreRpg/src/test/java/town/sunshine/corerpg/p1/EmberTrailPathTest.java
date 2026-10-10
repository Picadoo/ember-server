package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberTrailPathTest {
    @Test public void policies() {
        assertTrue(EmberTrailPath.shouldAuto(EmberTrailPath.AUTO));
        assertFalse(EmberTrailPath.shouldAuto(EmberTrailPath.ASK));
        assertTrue(EmberTrailPath.shouldMute(EmberTrailPath.MUTE));
        assertEquals(EmberTrailPath.AUTO, EmberTrailPath.parse("装上"));
    }
}
