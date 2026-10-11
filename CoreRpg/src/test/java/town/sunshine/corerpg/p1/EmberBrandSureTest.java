package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberBrandSureTest {
    @Test public void craftReady() {
        assertTrue(EmberBrandSure.craftReady(40, 40));
        assertFalse(EmberBrandSure.craftReady(39, 40));
    }
    @Test public void pinReady() {
        assertTrue(EmberBrandSure.pinReady(2, 2, 300, 300));
        assertFalse(EmberBrandSure.pinReady(1, 2, 300, 300));
        assertFalse(EmberBrandSure.pinReady(2, 2, 100, 300));
    }
}
