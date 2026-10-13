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

    @Test public void remainLineGapsAndOk() {
        String lack = EmberForgeRollRules.remainLine(0, 0, 0, 0, false);
        assertTrue(lack.contains("还差"));
        assertTrue(lack.contains("印记"));
        String ok = EmberForgeRollRules.remainLine(99, 99, 99, 99999, false);
        assertTrue(ok.contains("料够"));
        String floor = EmberForgeRollRules.remainLine(8, 0, 0, 0, true);
        assertTrue(floor.contains("还差") || floor.contains("料够"));
    }

    @Test public void forgerollFromAfkAndForge_mapsToMenus_D13325() {
        assertEquals("ember_p1_afk", EmberRunService.FORGEROLL_FROM.get("afk"));
        assertEquals("ember_p1_forge", EmberRunService.FORGEROLL_FROM.get("forge"));
        assertEquals("ember_p1_forgeroll", EmberRunService.FORGEROLL_MENU);
    }
}
