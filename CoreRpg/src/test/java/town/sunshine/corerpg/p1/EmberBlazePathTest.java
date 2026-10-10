package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBlazePathTest {
    @Test public void policies() {
        assertTrue(EmberBlazePath.shouldAuto(EmberBlazePath.AUTO));
        assertTrue(EmberBlazePath.shouldMute(EmberBlazePath.MUTE));
        assertEquals(EmberBlazePath.ASK, EmberBlazePath.parse("提醒"));
        assertEquals("auto", EmberBlazePath.key(EmberBlazePath.AUTO));
    }
}
