package town.sunshine.corerpg.p1;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

/** D430 随机锻造纯规则 */
public class EmberForgeRollRulesTest {
    @Test public void costsAndWeights() {
        assertEquals(8, EmberForgeRollRules.MARKS);
        assertEquals(4, EmberForgeRollRules.BLANKS);
        assertEquals(500, EmberForgeRollRules.COINS);
        assertEquals(0, EmberForgeRollRules.QUALITY_W[3]);
        assertEquals(4, EmberForgeRollRules.matCost().blanks);
        assertEquals(500, EmberForgeRollRules.matCost().coins);
    }

    @Test public void qualityNeverLegendary() {
        Random r = new Random(42);
        for (int i = 0; i < 5000; i++) {
            int q = EmberForgeRollRules.rollQuality(r);
            assertTrue(q >= 0 && q <= 2);
        }
    }

    @Test public void craftInRange() {
        Random r = new Random(7);
        for (int i = 0; i < 2000; i++) {
            int c = EmberForgeRollRules.rollCraft(r);
            assertTrue(c >= 0 && c <= 3);
        }
    }

    @Test public void refusalArmorAndBadTier() {
        assertTrue(EmberForgeRollRules.refusal(8, 1, "scorch", "helm", true).contains("blade"));
    }
}
