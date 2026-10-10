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

    @Test public void upgradeWhenOthersFullAndAffordable() {
        // q2 craft3 enhance max → only upgrade left if T1
        EmberItemData d = new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1, 2, 3,
                EmberTables.MAX_ENHANCE, 0, true, "drop", 1, 1);
        EmberUpgradeRules.Cost u = EmberUpgradeRules.upgradeCost(1);
        assertNotNull(u);
        String line = EmberForgeHeldNext.line(d, new EmberForgeHeldNext.Mats(u.shards, u.blanks, u.bone, u.coins), true);
        assertTrue(line.contains("升阶") || line.contains("upgrade") || line.contains("T1"));
    }
}
