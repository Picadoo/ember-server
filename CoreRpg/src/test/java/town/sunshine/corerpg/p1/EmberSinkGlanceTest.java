package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/** TIP-AM DESIGN §2.3: SinkGlance armor honesty pure helpers + blade costBits regression. */
public class EmberSinkGlanceTest {

    private static EmberItemData blade(int q, int craft) {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1, q, craft, 0, 0, true, "drop", 1, 1);
    }

    private static EmberItemData armor(String slot, String src, int tier) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId("scorch", slot, tier),
                "scorch", slot, tier, 1, 1, 0, 0, true, src, EmberItemData.DATA_VERSION, 0);
    }

    /** TIP-AM: armor shouldSkipForgeCosts. */
    @Test
    public void armorSkipsForgeCosts() {
        assertTrue(EmberSinkGlance.shouldSkipForgeCosts(armor("head", "drop", 2)));
        assertTrue(EmberSinkGlance.shouldSkipForgeCosts(armor("chest", "migrate", 1)));
        assertFalse(EmberSinkGlance.shouldSkipForgeCosts(blade(0, 0)));
        assertFalse(EmberSinkGlance.shouldSkipForgeCosts(null));
    }

    /** TIP-AM DESIGN §2.3: armorHonestChat contains ARMOR_REFUSE; no cost shapes. */
    @Test
    public void armorHonestChatHasRefuseNoCostShapes() {
        for (String slot : new String[]{"head", "chest", "legs", "boots"}) {
            EmberItemData a = armor(slot, "migrate", 2); // migrate → not dismantlable
            String chat = EmberSinkGlance.armorHonestChat(a);
            assertNotNull(chat);
            assertTrue(chat.contains(EmberUpgradeRules.ARMOR_REFUSE) || chat.contains("护甲随护符"));
            assertFalse("no 成色 cost for " + slot, chat.contains("成色 ") && chat.contains("→"));
            assertFalse("no 精工 cost for " + slot, chat.contains("精工 ") && chat.contains("→"));
            assertFalse("no 升阶 T for " + slot, chat.contains("升阶 T") || chat.contains("T1→") || chat.contains("T2→"));
            assertFalse(chat.contains("胚") && chat.contains("骨") && chat.contains("币"));
            String ab = EmberSinkGlance.armorHonestActionBar(a);
            assertNotNull(ab);
            assertTrue(ab.contains("护甲随护符"));
            assertFalse(ab.contains("料够"));
            assertFalse(ab.contains("缺少"));
        }
    }

    /** TIP-AM: dismantlable drop armor may add dismantle half; still no forge cost shapes. */
    @Test
    public void dismantlableArmorHonestChatHintsDismantle() {
        EmberItemData drop = armor("legs", "drop", 2);
        assertNull(EmberUpgradeRules.dismantleCheck(drop));
        assertTrue(EmberUpgradeRules.armorDismantleTenths(drop) > 0);
        String chat = EmberSinkGlance.armorHonestChat(drop);
        assertTrue(chat.contains(EmberUpgradeRules.ARMOR_REFUSE) || chat.contains("护甲随护符"));
        assertTrue(chat.contains("分解"));
        assertFalse(chat.contains("成色 ") && chat.contains("→"));
        assertFalse(chat.contains("精工 ") && chat.contains("→"));
        assertFalse(chat.contains("/corerpg p1 quality"));
        assertFalse(chat.contains("/corerpg p1 refine"));
        assertFalse(chat.contains("/corerpg p1 enhance"));
        assertFalse(chat.contains("/corerpg p1 upgrade"));
        String ab = EmberSinkGlance.armorHonestActionBar(drop);
        assertTrue(ab.contains("分解零头"));
    }

    /** TIP-AM DESIGN §2.3: blade costBits still carries 成色/精工 near-step shapes. */
    @Test
    public void bladeCostBitsStillHasQualityCraftShapes() {
        List<String> bits = EmberSinkGlance.costBits(blade(0, 0));
        String joined = String.join(" · ", bits);
        assertTrue(joined.contains("成色"));
        assertTrue(joined.contains("精工"));
        assertTrue(joined.contains("→") || joined.contains("胚"));
        assertFalse(EmberSinkGlance.shouldSkipForgeCosts(blade(0, 0)));
    }

    /** TIP-AM: forge entry honest pack alignment (reuse AL pack). */
    @Test
    public void forgeEntryHonestPackForbidsPreviewCmds() {
        String[][] pack = EmberForgeCommitNext.armorHonestPack();
        String joined = "";
        for (String[] btn : pack) joined += btn[1] + " ";
        assertFalse(joined.contains("/corerpg p1 enhance"));
        assertFalse(joined.contains("/corerpg p1 quality"));
        assertFalse(joined.contains("/corerpg p1 refine"));
        assertTrue(joined.contains("/corerpg p1 dismantle"));
        assertTrue(joined.contains("/corerpg p1 setchase"));
        assertTrue(joined.contains("/corerpg p1 forge"));
    }
}
