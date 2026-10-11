package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberEnhancePityChaseTest {
    @Test public void keys() {
        assertEquals("p1_enh_pity_chase", EmberEnhancePityChase.C_CHASE);
        assertEquals(50L, EmberEnhancePityChase.DELAY_TICKS);
    }
    @Test public void shouldChaseNull() {
        assertFalse(EmberEnhancePityChase.shouldChase(null));
    }
}
