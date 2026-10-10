package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberHopeMeterTest {

    private static EmberItemData bladeEnh(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 1, 0);
    }

    private static EmberItemData bladeQ(int q) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, q, 0, 0, 0, false, "drop", 1, 0);
    }

    private static EmberItemData bladeC(int craft) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, craft, 0, 0, false, "drop", 1, 0);
    }

    @Test public void glance_shared_style() {
        assertTrue(EmberHopeMeter.glance(2, 5, EmberHopeMeter.READY_ENHANCE).contains("2"));
        assertTrue(EmberHopeMeter.glance(2, 5, EmberHopeMeter.READY_ENHANCE).contains("/5"));
        assertFalse(EmberHopeMeter.glance(2, 5, EmberHopeMeter.READY_ENHANCE).contains("必成"));
        assertTrue(EmberHopeMeter.glance(5, 5, EmberHopeMeter.READY_ENHANCE).contains("下次必成"));
        assertTrue(EmberHopeMeter.glance(5, 5, EmberHopeMeter.READY_REROLL).contains("这次必出上限档"));
    }

    @Test public void enhance_mirrors_upgrade_rules() {
        String e0 = EmberHopeMeter.enhance(bladeEnh(0, 0));
        assertTrue(e0.contains("1/1"));
        assertTrue(e0.contains("下次必成"));
        String e3 = EmberHopeMeter.enhance(bladeEnh(3, 0));
        assertTrue(e3.contains("1"));
        assertTrue(e3.contains("/3"));
        assertFalse(e3.contains("下次必成"));
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

    @Test public void quality_forge_bypass_progress() {
        String q0 = EmberHopeMeter.quality(bladeQ(0));
        assertTrue(q0.contains("0/2"));
        assertTrue(q0.contains("确定升精良"));
        assertTrue(q0.contains("胚8"));
        String q1 = EmberHopeMeter.quality(bladeQ(1));
        assertTrue(q1.contains("1/2"));
        assertTrue(q1.contains("确定升卓越"));
        assertTrue(q1.contains("胚16"));
        String q2 = EmberHopeMeter.quality(bladeQ(2));
        assertTrue(q2.contains("2/2"));
        assertTrue(q2.contains("极品仅掉落"));
        assertFalse(q2.contains("确定升"));
        String q3 = EmberHopeMeter.quality(bladeQ(3));
        assertTrue(q3.contains("已满极品"));
    }

    @Test public void refine_forge_bypass_progress() {
        String c0 = EmberHopeMeter.refine(bladeC(0));
        assertTrue(c0.contains("0/3"));
        assertTrue(c0.contains("确定升2%"));
        assertTrue(c0.contains("胚3"));
        String c1 = EmberHopeMeter.refine(bladeC(1));
        assertTrue(c1.contains("1/3"));
        assertTrue(c1.contains("确定升4%"));
        String c2 = EmberHopeMeter.refine(bladeC(2));
        assertTrue(c2.contains("2/3"));
        assertTrue(c2.contains("确定升6%"));
        String c3 = EmberHopeMeter.refine(bladeC(3));
        assertTrue(c3.contains("3/3"));
        assertTrue(c3.contains("已满"));
    }
}
