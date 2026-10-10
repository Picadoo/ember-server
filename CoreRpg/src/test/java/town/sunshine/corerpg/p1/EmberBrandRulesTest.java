package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

/** D429 烙纹定向纯规则 */
public class EmberBrandRulesTest {
    @Test public void craftAndPinPrices() {
        assertEquals(40, EmberBrandRules.CRAFT_SHARDS);
        assertEquals("mat_ember_brand", EmberBrandRules.MAT_BRAND);
        assertEquals(2, EmberBrandRules.pinBrands(1));
        assertEquals(4, EmberBrandRules.pinBrands(2));
        assertEquals(6, EmberBrandRules.pinBrands(3));
        assertEquals(-1, EmberBrandRules.pinBrands(0));
        assertEquals(300, EmberBrandRules.pinCoins(1));
        assertEquals(1000, EmberBrandRules.pinCoins(3));
    }

    @Test public void charges() {
        assertEquals(13, EmberBrandRules.chargesLeft(0));
        assertEquals(1, EmberBrandRules.chargesLeft(12));
        assertEquals(0, EmberBrandRules.chargesLeft(13));
        assertEquals(0, EmberBrandRules.chargesLeft(99));
    }

    @Test public void pinRefusalNoTierZero() {
        assertTrue(EmberBrandRules.pinRefusal(0, 0, "b_affix", null, "blade").contains("不开"));
        assertTrue(EmberBrandRules.pinRefusal(1, 13, "b_affix", null, "blade").contains("用完"));
    }
}
