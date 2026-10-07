package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.p1.encounter.EmberSigAttunePreview;
import town.sunshine.corerpg.p1.encounter.EmberSigAttunePreview.Slot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** D297 W1a preview lines. */
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
}
