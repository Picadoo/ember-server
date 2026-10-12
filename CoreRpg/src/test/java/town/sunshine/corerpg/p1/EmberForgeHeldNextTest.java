package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberForgeHeldNextTest {
    private static EmberItemData blade(int q, int craft, int enh) {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1, q, craft, enh, 0, true, "drop", 1, 1);
    }

    private static EmberItemData armor(String slot, String src, int tier) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId("scorch", slot, tier),
                "scorch", slot, tier, 1, 1, 0, 0, true, src, EmberItemData.DATA_VERSION, 0);
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

    /** TIP-AK DESIGN §2.3: held armor never fakes quality/refine/enhance/upgrade. */
    @Test public void armorRefusesForgeProjection() {
        EmberForgeHeldNext.Mats rich = new EmberForgeHeldNext.Mats(9999, 9999, 9999, 9999);
        for (String slot : new String[]{"head", "chest"}) {
            EmberItemData a = armor(slot, "migrate", 2); // migrate → not dismantlable → ARMOR_REFUSE main
            String line = EmberForgeHeldNext.line(a, rich);
            assertTrue(line.contains(EmberUpgradeRules.ARMOR_REFUSE) || line.contains("护甲随护符"));
            assertFalse(line.contains("料够 · /corerpg p1 quality"));
            assertFalse(line.contains("/corerpg p1 refine"));
            assertFalse(line.contains("/corerpg p1 enhance"));
            assertFalse(line.contains("/corerpg p1 upgrade"));
            // forgeGoal must not override armor refuse
            String forced = EmberForgeHeldNext.line(a, rich, true, EmberForgeGoal.QUALITY);
            assertTrue(forced.contains(EmberUpgradeRules.ARMOR_REFUSE) || forced.contains("护甲随护符"));
            assertFalse(forced.contains("料够 · /corerpg p1 quality"));
        }
    }

    /** TIP-AK DESIGN §2.3: dismantlable drop armor may hint dismantle; still no forge push. */
    @Test public void dismantlableArmorHintsDismantle() {
        EmberItemData drop = armor("legs", "drop", 2);
        assertNull(EmberUpgradeRules.dismantleCheck(drop));
        assertTrue(EmberUpgradeRules.armorDismantleTenths(drop) > 0);
        String line = EmberForgeHeldNext.line(drop, new EmberForgeHeldNext.Mats(0, 0, 0, 0));
        assertTrue(line.contains("分解"));
        assertFalse(line.contains("成色"));
        assertFalse(line.contains("强化"));
        assertFalse(line.contains("/corerpg p1 quality"));
        assertFalse(line.contains("/corerpg p1 enhance"));
        assertFalse(line.contains("/corerpg p1 refine"));
        assertFalse(line.contains("/corerpg p1 upgrade"));
    }
}
