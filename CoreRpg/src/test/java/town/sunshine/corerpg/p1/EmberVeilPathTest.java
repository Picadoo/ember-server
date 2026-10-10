package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberVeilPathTest {
    @Test public void policies() {
        assertTrue(EmberVeilPath.shouldAuto(EmberVeilPath.AUTO));
        assertTrue(EmberVeilPath.shouldMute(EmberVeilPath.MUTE));
        assertEquals(EmberVeilPath.ASK, EmberVeilPath.parse("提醒"));
        assertEquals("auto", EmberVeilPath.key(EmberVeilPath.AUTO));
    }
}
