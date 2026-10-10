package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberForgeHeldNextTest {
    private static EmberItemData blade(int q, int craft, int enh) {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1, q, craft, enh, 0, true, "drop", 1, 1);
    }

    @Test public void prefersAffordableQuality() {
        EmberItemData d = blade(0, 0, 0);
        EmberUpgradeRules.Cost qc = EmberUpgradeRules.qualityCost(0);
        assertNotNull(qc);
        String line = EmberForgeHeldNext.line(d, new EmberForgeHeldNext.Mats(0, qc.blanks, qc.bone, qc.coins));
        assertTrue(line.contains("成色"));
        assertTrue(line.contains("料够"));
        assertTrue(line.contains("quality"));
    }

    @Test public void emptyHand() {
        assertTrue(EmberForgeHeldNext.line(null, new EmberForgeHeldNext.Mats(0,0,0,0)).contains("空手"));
    }

    @Test public void nearestGapWhenBroke() {
        EmberItemData d = blade(0, 0, 0);
        String line = EmberForgeHeldNext.line(d, new EmberForgeHeldNext.Mats(0, 0, 0, 0));
        assertTrue(line.contains("下一确定"));
        assertTrue(line.contains("还差料") || line.contains("料够"));
    }
}
