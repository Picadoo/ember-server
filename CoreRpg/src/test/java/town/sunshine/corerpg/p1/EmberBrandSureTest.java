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
    }
    @Test public void pathHelpers() {
        assertEquals("b_affix", EmberBrandPath.pinAffixId(EmberBrandPath.HUNT));
        assertEquals("blade", EmberBrandPath.pinSlot(EmberBrandPath.HUNT));
        assertEquals("charm", EmberBrandPath.pinSlot(EmberBrandPath.BIND));
        assertEquals("p1_brand_auto_pin_", EmberBrandSure.C_AUTO_PIN);
    }
}
