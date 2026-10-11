package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberMarksBreakTest {
    @Test
    public void needTierProgression() {
        assertEquals(1, EmberMarksBreak.needTier(0));
        assertEquals(2, EmberMarksBreak.needTier(1));
        assertEquals(3, EmberMarksBreak.needTier(2));
        assertEquals(0, EmberMarksBreak.needTier(3));
    }

    @Test
    public void needEnhanceForIIAndIII() {
        assertEquals(0, EmberMarksBreak.needEnhance(0));
        assertEquals(6, EmberMarksBreak.needEnhance(1));
        assertEquals(9, EmberMarksBreak.needEnhance(2));
    }

    @Test
    public void pickSlotPrefersLaggard() {
        EmberItemData blade = EmberItemData.create("scorch", "blade", 2, 1, 0, 6, true, "drop");
        EmberItemData charm = EmberItemData.create("scorch", "charm", 1, 1, 0, 6, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(EmberTables.defaults(), blade, charm, 20);
        assertEquals("scorch", lo.activeSet);
        assertEquals(1, lo.awakening); // min tier 1
        // next need T2 — charm is T1 lagging
        assertEquals("charm", EmberMarksBreak.pickSlot(lo));
    }

    @Test
    public void pickFamilyUsesActiveSet() {
        EmberItemData blade = EmberItemData.create("burst", "blade", 2, 1, 0, 6, true, "drop");
        EmberItemData charm = EmberItemData.create("burst", "charm", 2, 1, 0, 6, true, "drop");
        EmberLoadout lo = EmberLoadout.compute(EmberTables.defaults(), blade, charm, 20);
        assertEquals("burst", EmberMarksBreak.pickFamily(lo, null));
    }
}
