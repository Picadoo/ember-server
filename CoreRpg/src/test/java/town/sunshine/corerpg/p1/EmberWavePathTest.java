package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberWavePathTest {
    @Test public void policies() {
        assertTrue(EmberWavePath.shouldAuto(EmberWavePath.AUTO));
        assertTrue(EmberWavePath.shouldMute(EmberWavePath.MUTE));
        assertEquals(EmberWavePath.ASK, EmberWavePath.parse("提醒"));
        assertEquals("auto", EmberWavePath.key(EmberWavePath.AUTO));
    }
}
