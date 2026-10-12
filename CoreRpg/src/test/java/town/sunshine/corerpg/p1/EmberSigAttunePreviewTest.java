package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.p1.encounter.EmberSigAttunePreview;
import town.sunshine.corerpg.p1.encounter.EmberSigAttunePreview.Slot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** D297 W1a / D12622 attune preview lines (six-slot label align). */
public class EmberSigAttunePreviewTest {

    @Test public void runLine_D297_bladeAndCharm() {
        assertEquals("", EmberSigAttunePreview.runLine(null));
        assertEquals("", EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(null, null)));
        String s = EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(
                new Slot("刃", "调律", "对首领×0.98", "被预警+4%"),
                new Slot("护符", "原版", "被预警+4%", "对首领×0.98")));
        assertTrue(s.contains("本局生效"));
        assertTrue(s.contains("刃·调律"));
        assertTrue(s.contains("护符·原版"));
    }

    @Test public void costLine_D297_tagsEdition() {
        String c = EmberSigAttunePreview.costLine(EmberSigAttunePreview.listOf(
                new Slot("刃", "调律", "对首领×0.98", null), null));
        assertTrue(c.contains("对首领×0.98"));
        assertTrue(c.contains("（调）"));
    }

    /** DESIGN §2.4: charm-only (blade+charm) — same shape as before. */
    @Test public void runLine_D12622_charmOnly_bladeAndCharm() {
        String s = EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(
                new Slot("刃", "原版", "对首领×0.98", null),
                new Slot("护符", "调律", "被预警+4%", null)));
        assertTrue(s.contains("刃·"));
        assertTrue(s.contains("护符·"));
        assertFalse(s.contains("头盔"));
        assertTrue(s.startsWith("§7本局生效："));
    }

    /** DESIGN §2.4: charm empty, head wins → 头盔·, no 护符·. */
    @Test public void runLine_D12622_headWin_noCharmLabel() {
        String s = EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(
                new Slot("刃", "原版", "对首领×0.98", null),
                new Slot("头盔", "调律", "被预警+4%", null)));
        assertTrue(s.contains("刃·"));
        assertTrue(s.contains("头盔·"));
        assertFalse(s.contains("护符"));
    }

    /** DESIGN §2.4: charm+head both signed → preview list only carries winner (护符); no 头盔. */
    @Test public void runLine_D12622_prioCharmOverHead() {
        // GrowthService passes only the prio winner as defSlot; Preview must not invent a third segment.
        String s = EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(
                new Slot("刃", "原版", "x", null),
                new Slot("护符", "原版", "y", null)));
        assertTrue(s.contains("护符·"));
        assertFalse(s.contains("头盔"));
        assertEquals(1, countSeg(s, "§8/ ")); // one separator → exactly two segments
    }

    /** DESIGN §2.4: dual armor no charm → only high-prio armor label (head over boots). */
    @Test public void runLine_D12622_dualArmor_headOnly() {
        String s = EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(
                new Slot("刃", "原版", "x", null),
                new Slot("头盔", "原版", "y", null)));
        assertTrue(s.contains("头盔·"));
        assertFalse(s.contains("靴子"));
        assertFalse(s.contains("护符"));
    }

    /** DESIGN §2.4: Q07 off / no slots → empty string (both keys). */
    @Test public void runLine_D12622_q07Off_empty() {
        assertEquals("", EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(null, null)));
        assertEquals("", EmberSigAttunePreview.costLine(EmberSigAttunePreview.listOf(null, null)));
        assertEquals("", EmberSigAttunePreview.runLine(null));
        assertEquals("", EmberSigAttunePreview.costLine(null));
    }

    /** DESIGN §2.4: pure copy — list with 胸甲·调律 appears in runLine. */
    @Test public void runLine_D12622_chestAttuneLabel() {
        String s = EmberSigAttunePreview.runLine(EmberSigAttunePreview.listOf(
                null, new Slot("胸甲", "调律", "代价甲", null)));
        assertTrue(s.contains("胸甲·调律"));
        assertTrue(s.startsWith("§7本局生效："));
        String c = EmberSigAttunePreview.costLine(EmberSigAttunePreview.listOf(
                null, new Slot("胸甲", "调律", "代价甲", null)));
        assertTrue(c.contains("代价甲"));
        assertTrue(c.contains("（调）"));
        assertTrue(c.startsWith("§8代价："));
    }

    private static int countSeg(String s, String sep) {
        int n = 0, i = 0;
        while ((i = s.indexOf(sep, i)) >= 0) { n++; i += sep.length(); }
        return n;
    }
}
