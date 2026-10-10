package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberModePathTest {
    @Test public void maps() {
        assertTrue(EmberModePath.isModeUnlockMap("q04"));
        assertTrue(EmberModePath.isModeUnlockMap("q05"));
        assertTrue(EmberModePath.isModeUnlockMap("q06"));
        assertFalse(EmberModePath.isModeUnlockMap("q01"));
        assertEquals("p1_mode_path_offered_", EmberModePath.C_OFFERED);
    }
}
