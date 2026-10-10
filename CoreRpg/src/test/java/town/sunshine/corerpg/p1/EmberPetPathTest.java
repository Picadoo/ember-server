package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPetPathTest {
    @Test public void policies() {
        assertTrue(EmberPetPath.shouldAuto(EmberPetPath.AUTO));
        assertFalse(EmberPetPath.shouldAuto(EmberPetPath.ASK));
        assertTrue(EmberPetPath.shouldMute(EmberPetPath.MUTE));
        assertEquals(EmberPetPath.AUTO, EmberPetPath.parse("出战"));
    }
}
