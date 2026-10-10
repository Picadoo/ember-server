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
}
