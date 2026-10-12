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

    @Test public void sig_chase_hope_progress() {
        EmberSignature.Def d = EmberSignature.byId("L01");
        String mid = EmberHopeMeter.sigChase(d, 2);
        assertTrue(mid.contains("2/5"));
        assertTrue(mid.contains("不锁通关"));
        assertTrue(mid.contains("满可烙印")); // under-mark keeps vague phrase
        assertFalse(mid.contains("5/5"));
        String ready = EmberHopeMeter.sigChase(d, 5);
        assertTrue(ready.contains("5/5"));
        assertTrue(ready.contains("可烙刃")); // lo==null → def.slot fallback (TIP-AO)
        assertFalse(ready.contains("满可烙印"));
        assertTrue(ready.contains("不锁通关"));
        assertTrue(EmberHopeMeter.sigChase(null, 0).contains("未选"));
    }

    /** DESIGN §2.3 TIP-AO: L02 + head worn · full → 头盔; empty → 缺可烙穿着件; under-mark unchanged. */
    @Test public void sigChase_D12737_readyTail_defense() {
        EmberTables tables = EmberTables.defaults();
        EmberSignature.Def l02 = EmberSignature.byId("L02");
        int need = EmberSignature.imprintMarks();
        EmberItemData head = EmberItemData.create("scorch", "head", 2, 1, 0, 0, true, "drop");
        EmberLoadout loHead = EmberLoadout.compute(tables, null, null, 10, 0, 0,
                new EmberItemData[]{head, null, null, null});
        String withHead = EmberHopeMeter.sigChase(l02, need, loHead);
        assertTrue(withHead, withHead.contains("头盔") || withHead.contains("头"));
        assertFalse(withHead, withHead.contains("满可烙印"));
        assertFalse("must not force 护符 when armor wins", withHead.contains("可烙护符"));

        EmberLoadout empty = EmberLoadout.compute(tables, null, null, 10);
        String miss = EmberHopeMeter.sigChase(l02, need, empty);
        assertTrue(miss, miss.contains("缺可烙穿着件"));

        EmberSignature.Def l01 = EmberSignature.byId("L01");
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 0, true, "drop");
        EmberLoadout blo = EmberLoadout.compute(tables, blade, null, 10);
        String bladeReady = EmberHopeMeter.sigChase(l01, need, blo);
        assertTrue(bladeReady, bladeReady.contains("可烙刃"));

        String under = EmberHopeMeter.sigChase(l02, need - 1, loHead);
        assertTrue(under, under.contains("满可烙印"));
        assertFalse(under, under.contains("缺可烙穿着件"));
        assertFalse(under, under.contains("可烙头"));
    }
    @Test
    public void mapGearIdentity_nullMap() {
        String s = EmberHopeMeter.mapGearIdentity(null, null);
        assertTrue(s.contains("不锁通关"));
    }

    @Test
    public void mapGearIdentity_familyProgress() {
        java.util.Map<String, Object> m = new java.util.HashMap<String, Object>();
        m.put("name", "灰坡");
        m.put("tier", 1);
        m.put("drop_label", "T1");
        java.util.Map<String, Object> loot = new java.util.HashMap<String, Object>();
        loot.put("family", EmberRunRules.FAMILIES[0]);
        loot.put("slot", "blade");
        m.put("loot", loot);
        EmberRunMaps.MapDef d = new EmberRunMaps.MapDef("q01", m);
        String s = EmberHopeMeter.mapGearIdentity(d, null);
        assertTrue(s.contains("图鉴 0/2"));
        assertTrue(s.contains("不锁通关"));
        assertTrue(s.contains("每局1装"));
    }

    @Test
    public void imprint_attune_spend_hope() {
        String mid = EmberHopeMeter.imprintSpend(2, 5, 100, 300);
        assertTrue(mid.contains("/5"));
        assertTrue(mid.contains("币"));
        String ready = EmberHopeMeter.imprintSpend(5, 5, 300, 300);
        assertTrue(ready.contains("5/5"));
        assertTrue(ready.contains("徽记够"));
        String a = EmberHopeMeter.attuneUnlock(3, 10);
        assertTrue(a.contains("3") && a.contains("10"));
        assertTrue(EmberHopeMeter.attuneUnlock(10, 10).contains("Shift解锁调律"));
    }

    @Test
    public void mapGearBoard_nullMaps() {
        assertTrue(EmberHopeMeter.mapGearBoard(null).contains("未载入"));
    }

}
