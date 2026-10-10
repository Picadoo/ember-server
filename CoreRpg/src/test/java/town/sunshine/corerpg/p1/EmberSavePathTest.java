package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberSavePathTest {
    @Test public void policies() {
        assertTrue(EmberSavePath.shouldAuto(EmberSavePath.AUTO));
        assertTrue(EmberSavePath.shouldMute(EmberSavePath.MUTE));
        assertEquals(EmberSavePath.ASK, EmberSavePath.parse("提醒"));
        assertEquals("auto", EmberSavePath.key(EmberSavePath.AUTO));
    }
}
