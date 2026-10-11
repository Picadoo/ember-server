package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberForgeChargeRestTest {
    @Test public void keys() {
        assertEquals("p1_afc_rest_", EmberForgeChargeRest.C_REST);
        assertEquals(13, EmberBrandRules.MAX_CHARGES);
        assertEquals(0, EmberBrandRules.chargesLeft(13));
        assertEquals(1, EmberBrandRules.chargesLeft(12));
    }
}
