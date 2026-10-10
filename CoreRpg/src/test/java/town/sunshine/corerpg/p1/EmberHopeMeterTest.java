package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberHopeMeterTest {

    private static EmberItemData bladeEnh(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 1, 0);
    }

    @Test public void glance_shared_style() {
        assertTrue(EmberHopeMeter.glance(2, 5, EmberHopeMeter.READY_ENHANCE).contains("2"));
        assertTrue(EmberHopeMeter.glance(2, 5, EmberHopeMeter.READY_ENHANCE).contains("/5"));
        assertFalse(EmberHopeMeter.glance(2, 5, EmberHopeMeter.READY_ENHANCE).contains("必成"));
        assertTrue(EmberHopeMeter.glance(5, 5, EmberHopeMeter.READY_ENHANCE).contains("下次必成"));
        assertTrue(EmberHopeMeter.glance(5, 5, EmberHopeMeter.READY_REROLL).contains("这次必出上限档"));
    }

    @Test public void enhance_mirrors_upgrade_rules() {
        // +0→+1 always guaranteed (1/1)
        String e0 = EmberHopeMeter.enhance(bladeEnh(0, 0));
        assertTrue(e0.contains("1/1"));
        assertTrue(e0.contains("下次必成"));
        // +3→+4 MAX_TRIES=3; pity 0 → attempt 1/3
        String e3 = EmberHopeMeter.enhance(bladeEnh(3, 0));
        assertTrue(e3.contains("1"));
        assertTrue(e3.contains("/3"));
        assertFalse(e3.contains("下次必成"));
        // pity 2 → attempt 3/3 ready
        String ready = EmberHopeMeter.enhance(bladeEnh(3, 2));
        assertTrue(ready.contains("3/3"));
        assertTrue(ready.contains("下次必成"));
        assertTrue(EmberHopeMeter.enhance(bladeEnh(10, 0)).contains("已满"));
    }

    @Test public void reroll_delegate_unchanged() {
        assertEquals(EmberHopeMeter.glance(0, 5, EmberHopeMeter.READY_REROLL), EmberRerollPity.glance(0, 5));
        assertEquals(EmberHopeMeter.glance(5, 5, EmberHopeMeter.READY_REROLL), EmberRerollPity.glance(5, 5));
        assertEquals(EmberHopeMeter.emptySlot("刃"), EmberRerollPity.emptySlot("刃"));
    }
}
