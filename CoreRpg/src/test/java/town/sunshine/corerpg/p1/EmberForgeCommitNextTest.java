package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

/** D2086: piece-aware button helpers pure. */
public class EmberForgeCommitNextTest {

    private static EmberItemData blade(int q, int craft) {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 2), "scorch", "blade", 2, q, craft, 0, 0, true, "drop", 1, 1);
    }

    @Test
    public void qualityBtnWhenOpen() {
        String[] b = EmberForgeCommitNext.quality(blade(0, 0));
        assertNotNull(b);
        assertEquals("[去成色]", b[0]);
    }

    @Test
    public void qualityBtnNullWhenMax() {
        assertNull(EmberForgeCommitNext.quality(blade(2, 0)));
    }

    @Test
    public void refineBtnWhenOpen() {
        String[] b = EmberForgeCommitNext.refine(blade(0, 0));
        assertNotNull(b);
        assertEquals("[去精工]", b[0]);
    }

    @Test
    public void enhanceAlways() {
        assertEquals("/corerpg p1 enhance", EmberForgeCommitNext.enhance()[1]);
    }

    @Test
    public void followHintsMatchLoops() {
        assertTrue(EmberQualityLoop.followHint().contains("精工"));
        assertTrue(EmberRefineLoop.followHint(false).contains("强化"));
    }

    @Test
    public void forgerollFollowHintMentionsDeliver() {
        assertTrue(EmberForgeRollLoop.followHint(false).contains("装备库到账"));
        assertTrue(EmberForgeRollLoop.followHint(false).contains("去强化"));
    }

    @Test
    public void commitCmdsPointAtP1() {
        assertEquals("/corerpg p1 enhance", EmberForgeCommitNext.enhance()[1]);
        assertEquals("/corerpg p1 quality", EmberForgeCommitNext.quality(blade(0, 0))[1]);
        assertEquals("/corerpg p1 refine", EmberForgeCommitNext.refine(blade(0, 0))[1]);
    }

    private static EmberItemData armor(String slot, int q, int craft) {
        return new EmberItemData("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                EmberItemData.templateId("scorch", slot, 2), "scorch", slot, 2, q, craft, 0, 0, true, "drop", 1, 1);
    }

    /** TIP-AL DESIGN §2.3: armor quality/refine → null. */
    @Test
    public void armorQualityRefineNull() {
        for (String slot : new String[]{"head", "chest", "legs", "boots"}) {
            EmberItemData a = armor(slot, 0, 0);
            assertTrue(a.isArmor());
            assertNull("quality null for " + slot, EmberForgeCommitNext.quality(a));
            assertNull("refine null for " + slot, EmberForgeCommitNext.refine(a));
        }
    }

    /** TIP-AL DESIGN §2.3: blade quality/refine still open at q0/c0 (regression). */
    @Test
    public void bladeQualityRefineStillOpen() {
        assertNotNull(EmberForgeCommitNext.quality(blade(0, 0)));
        assertNotNull(EmberForgeCommitNext.refine(blade(0, 0)));
        assertEquals("/corerpg p1 quality", EmberForgeCommitNext.quality(blade(0, 0))[1]);
        assertEquals("/corerpg p1 refine", EmberForgeCommitNext.refine(blade(0, 0))[1]);
    }

    /** TIP-AL DESIGN §2.3: armorHonestPack has no enhance/quality/refine/upgrade cmds. */
    @Test
    public void armorHonestPackForbidsForgeProgressCmds() {
        String[][] pack = EmberForgeCommitNext.armorHonestPack();
        assertEquals(3, pack.length);
        String joined = "";
        for (String[] btn : pack) {
            assertNotNull(btn);
            assertTrue(btn.length >= 2);
            joined += btn[1] + " ";
        }
        assertFalse(joined.contains("/corerpg p1 enhance"));
        assertFalse(joined.contains("/corerpg p1 quality"));
        assertFalse(joined.contains("/corerpg p1 refine"));
        assertFalse(joined.contains("/corerpg p1 upgrade"));
        assertTrue(joined.contains("/corerpg p1 dismantle"));
        assertTrue(joined.contains("/corerpg p1 setchase"));
        assertTrue(joined.contains("/corerpg p1 forge"));
    }

    /** TIP-AL DESIGN §2.3: enhance() no-item API retained. */
    @Test
    public void enhanceNoItemApiRetained() {
        assertEquals("/corerpg p1 enhance", EmberForgeCommitNext.enhance()[1]);
        assertEquals("[去强化]", EmberForgeCommitNext.enhance()[0]);
    }
}
