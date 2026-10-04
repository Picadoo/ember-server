package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/** D174 签名传奇: stamp roll, active rules (≤ 2, same tag, talent exclusion, set binding, dual unlock), imprint precheck, grants. */
public class EmberSignatureTest {

    private static EmberItemData item(String uid, String fam, String slot, int tier) {
        return new EmberItemData(uid, EmberItemData.templateId(fam, slot, tier), fam, slot, tier, 0, 0, 0, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    private static EmberSignature.Worn w(String id, String slot, String fam) {
        return new EmberSignature.Worn(EmberSignature.byId(id), slot, fam);
    }

    @Test
    public void codesAreUniqueAndStable() {
        Set<Integer> codes = new HashSet<Integer>();
        Set<String> ids = new HashSet<String>();
        for (EmberSignature.Def d : EmberSignature.DEFS) {
            assertTrue(d.code > 0 && codes.add(d.code));
            assertTrue(ids.add(d.id));
            assertTrue("blade".equals(d.slot) || "charm".equals(d.slot));
            assertFalse(d.mods.isEmpty());
        }
        assertEquals(1, EmberSignature.byId("L01").code); // stored in saves: never renumber
        assertEquals("L06", EmberSignature.byCode(6).id);
        assertEquals("L12", EmberSignature.byCode(12).id);
        assertEquals(Arrays.asList("q01", "q02", "q03", "q04", "q05", "q06", "q07"), EmberSignature.maps());
        assertEquals(2, EmberSignature.forMap("q02").size());
        assertEquals(2, EmberSignature.forMap("q06").size());
        assertEquals(3, EmberSignature.forMap("q07").size()); // stage 3 (D184): L13 blade + L14 / L15 charms
        assertEquals("L15", EmberSignature.byCode(15).id);
        assertEquals(15, EmberSignature.DEFS.size());
        assertFalse(EmberSignature.hasMap("q08"));
    }

    @Test
    public void stampRollRateAndFit() {
        // Q01: blade L01 is scorch-only, charm L02 any family
        assertEquals("L01", EmberSignature.rollStamp("q01", "blade", "scorch", 0.0).id);
        assertNull(EmberSignature.rollStamp("q01", "blade", "burst", 0.0));
        assertEquals("L02", EmberSignature.rollStamp("q01", "charm", "burst", 0.119).id);
        assertNull(EmberSignature.rollStamp("q01", "charm", "burst", EmberSignature.STAMP_RATE));
        assertNull(EmberSignature.rollStamp("q01", "charm", "burst", Double.NaN));
        assertEquals("L13", EmberSignature.rollStamp("q07", "blade", "scorch", 0.0).id);   // stage 3: any-family blade
        assertEquals("L14", EmberSignature.rollStamp("q07", "charm", "burst", 0.119).id);  // only the any-family charm fits burst
        assertEquals("L14", EmberSignature.rollStamp("q07", "charm", "scorch", 0.0).id);   // scorch charm: L14 or L15, uniform
        assertEquals("L15", EmberSignature.rollStamp("q07", "charm", "scorch", 0.119).id);
        assertNull(EmberSignature.rollStamp("q08", "blade", "scorch", 0.0));
        assertEquals("L07", EmberSignature.rollStamp("q04", "blade", "burst", 0.0).id);   // any-family blade
        assertNull(EmberSignature.rollStamp("q04", "charm", "burst", 0.0));               // L08 is scorch-only
        int hit = 0, n = 100000;
        java.util.Random r = new java.util.Random(5);
        for (int i = 0; i < n; i++) if (EmberSignature.rollStamp("q03", "charm", "sustain", r.nextDouble()) != null) hit++;
        assertEquals(EmberSignature.STAMP_RATE, hit / (double) n, 0.005);
    }

    @Test
    public void activeRules() {
        List<String> none = Collections.emptyList();
        // blade scorch L01 needs the scorch set
        assertEquals(1, EmberSignature.active(w("L01", "blade", "scorch"), null, "scorch", none, true).size());
        assertEquals(0, EmberSignature.active(w("L01", "blade", "scorch"), null, "none", none, true).size());
        // two: blade + charm of different tags, only after the dual unlock
        assertEquals(2, EmberSignature.active(w("L01", "blade", "scorch"), w("L02", "charm", "scorch"), "scorch", none, true).size());
        List<EmberSignature.Def> one = EmberSignature.active(w("L01", "blade", "scorch"), w("L02", "charm", "scorch"), "scorch", none, false);
        assertEquals(1, one.size());
        assertEquals("L01", one.get(0).id);
        // before dual: charm alone still works when the blade has none / an inactive one
        assertEquals("L02", EmberSignature.active(w("L01", "blade", "scorch"), w("L02", "charm", "burst"), "none", none, false).get(0).id);
        // talent of the same tag switches the signature off
        assertEquals(1, EmberSignature.active(w("L01", "blade", "scorch"), w("L02", "charm", "scorch"), "scorch", Arrays.asList("t1c"), true).size());
        assertEquals("与已选天赋同类，不叠加", EmberSignature.offReason(w("L03", "charm", "burst"), null, "burst", Arrays.asList("t3b"), true));
        // wrong slot / family never counts
        assertNotNull(EmberSignature.offReason(w("L01", "charm", "scorch"), null, "scorch", none, true));
        assertNotNull(EmberSignature.offReason(w("L04", "blade", "scorch"), null, "scorch", none, true));
    }

    @Test
    public void sameTagOnlyBlade() {
        // no stage-1 pair shares a tag; check the rule on a synthetic tag clash via offReason order (blade first)
        EmberSignature.Def b = EmberSignature.byId("L04"), c = EmberSignature.byId("L03");
        assertFalse(b.tag.equals(c.tag));
        // one blade per boss; two charms only on Q07 (stage 3: L14 any + L15 scorch)
        for (EmberSignature.Def x : EmberSignature.DEFS) for (EmberSignature.Def y : EmberSignature.DEFS)
            if (x != y && x.slot.equals(y.slot) && x.map.equals(y.map)) assertTrue(x.id + "/" + y.id, "q07".equals(x.map) && "charm".equals(x.slot));
        // L14 shares the guard tag with L11: with L11 on the blade only the blade's counts; L15 (burn2) stacks with L01 (burn)
        List<String> none = Collections.emptyList();
        assertEquals(1, EmberSignature.active(w("L11", "blade", "burst"), w("L14", "charm", "burst"), "burst", none, true).size());
        assertEquals(2, EmberSignature.active(w("L01", "blade", "scorch"), w("L15", "charm", "scorch"), "scorch", none, true).size());
        assertEquals(1, EmberSignature.active(w("L13", "blade", "burst"), w("L15", "charm", "burst"), "burst", none, true).size()); // L15 scorch-only
    }

    @Test
    public void stage2aRules() {
        List<String> none = Collections.emptyList();
        // L10 shares the dodge_burst key with L04 → same tag: with L04 on the blade only the blade's counts
        List<EmberSignature.Def> a = EmberSignature.active(w("L04", "blade", "burst"), w("L10", "charm", "burst"), "burst", none, true);
        assertEquals(1, a.size());
        assertEquals("L04", a.get(0).id);
        // L10 has hit_burst like the talent 反震 (t3b) → off while t3b is picked
        assertEquals("与已选天赋同类，不叠加", EmberSignature.offReason(w("L10", "charm", "burst"), null, "burst", Arrays.asList("t3b"), true));
        // shape blade + scorch charm (different tags) both count; L08 needs the scorch set
        assertEquals(2, EmberSignature.active(w("L07", "blade", "scorch"), w("L08", "charm", "scorch"), "scorch", none, true).size());
        assertEquals(1, EmberSignature.active(w("L07", "blade", "burst"), w("L08", "charm", "scorch"), "burst", none, true).size());
        // L01 (burn) + L08 (burn): only the blade's
        assertEquals("L01", EmberSignature.active(w("L01", "blade", "scorch"), w("L08", "charm", "scorch"), "scorch", none, true).get(0).id);
        assertEquals(1, EmberSignature.active(w("L01", "blade", "scorch"), w("L08", "charm", "scorch"), "scorch", none, true).size());
        // combined mods: sizes / caps never add (MAXK), skill_var adds, multipliers multiply
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(Arrays.asList(EmberSignature.byId("L11").mods, EmberSignature.byId("L08").mods));
        java.util.Map<String, Double> l11 = EmberSignature.byId("L11").mods, l08 = EmberSignature.byId("L08").mods;
        assertEquals(l11.get("skill_shield_max"), m.get("skill_shield_max"), 1e-9);
        assertEquals(1.0, m.get("skill_ignite_n"), 1e-9);
        assertEquals(2.0, m.get("skill_var"), 1e-9);
        assertEquals(l11.getOrDefault("skill_mult", 1.0) * l08.getOrDefault("skill_mult", 1.0), m.get("skill_mult"), 1e-9);
        assertEquals(0.0, EmberGrowth.Mods.NONE.get("skill_cap"), 1e-9); // absent = off
        assertEquals(1.0, EmberGrowth.Mods.NONE.get("skill_mult"), 1e-9);
    }

    @Test
    public void lowHpSustainInterval() {
        EmberSetEngine e = new EmberSetEngine();
        e.setLoadout("sustain", 1);
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(Arrays.asList(EmberSignature.byId("L12").mods));
        e.setTune(new EmberSetEngine.Tune(0, (int) Math.round(m.get("sustain_every")), 1, 1, m.get("sustain_mult"), 0, 0, 0,
                m.get("sustain_low"), (int) Math.round(m.get("sustain_low_every"))));
        assertEquals(5, e.everyAt(0.9));
        assertEquals(5, e.everyAt(0.4));
        assertEquals(3, e.everyAt(0.39));
        // three valid swings below 40% HP heal; above 40% the third does not
        long t = 1_000_000L;
        EmberSetEngine.Outcome o = null;
        for (int i = 1; i <= 3; i++) {
            EmberSetEngine.Hit h = EmberSetEngine.Hit.melee(i, 1.0, 5, "m");
            h.hpFrac = 0.3;
            o = e.onHit(h, t + i, 10, 100);
        }
        assertEquals(EmberSetEngine.Trigger.HEAL, o.trigger);
        assertEquals(0.99 * EmberSetRules.sustainPct(1) * 100, o.amount, 1e-9);
        EmberSetEngine f = new EmberSetEngine();
        f.setLoadout("sustain", 1);
        f.setTune(e.tune());
        for (int i = 1; i <= 3; i++) {
            EmberSetEngine.Hit h = EmberSetEngine.Hit.melee(100 + i, 1.0, 5, "m");
            h.hpFrac = 0.8;
            o = f.onHit(h, t + i, 10, 100);
        }
        assertEquals(EmberSetEngine.Trigger.NONE, o.trigger);
    }

    @Test
    public void imprintCheck() {
        EmberSignature.Def l01 = EmberSignature.byId("L01"), l06 = EmberSignature.byId("L06");
        EmberItemData sb = item("a", "scorch", "blade", 2), bb = item("b", "burst", "blade", 1), ch = item("c", "sustain", "charm", 3);
        assertNull(EmberSignature.imprintCheck(l01, sb, true, true, 5, 600, 0));
        assertNotNull(EmberSignature.imprintCheck(l01, sb, false, true, 5, 600, 0));   // Q02 not cleared
        assertNotNull(EmberSignature.imprintCheck(l01, sb, true, false, 5, 600, 0));   // Q01 not cleared
        assertNotNull(EmberSignature.imprintCheck(l01, sb, true, true, 4, 600, 0));    // insignia short
        assertNotNull(EmberSignature.imprintCheck(l01, sb, true, true, 5, 599, 0));    // 300 × T2
        assertNotNull(EmberSignature.imprintCheck(l01, bb, true, true, 5, 600, 0));    // scorch-only
        assertNotNull(EmberSignature.imprintCheck(l01, sb, true, true, 5, 600, 1));    // already L01
        assertNotNull(EmberSignature.imprintCheck(l01, item("t", "none", "blade", 0), true, true, 5, 600, 0));
        assertNull(EmberSignature.imprintCheck(l06, ch, true, true, 5, 900, 2));      // any family, overwrite allowed
        assertEquals(900, EmberSignature.imprintCoin(3));
    }

    @Test
    public void grantsRoundTripAndDeterministic() {
        EmberRunRules.Grant g = EmberRunRules.Grant.decode("sig_mark", new EmberRunRules.Grant("sig_mark", EmberRunRules.Kind.SIGMARK, "q02", 1, null).encode());
        assertEquals(EmberRunRules.Kind.SIGMARK, g.kind);
        assertEquals("q02", g.id);
        assertEquals(1, g.amount);
        EmberRunRules.Grant s = EmberRunRules.Grant.decode("sig_stamp", "sig:0123456789abcdef0123456789abcdef/L03");
        assertEquals(EmberRunRules.Kind.SIG, s.kind);
        assertEquals("0123456789abcdef0123456789abcdef/L03", s.id);
        assertNull(EmberRunRules.Grant.decode("sig_stamp", "sig:broken"));

        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.seed = 42; in.player = "p"; in.runId = "r";
        EmberRunRules.Grant base = new EmberRunRules.Grant("base_item", EmberRunRules.Kind.ITEM, "u1", 1,
                new EmberRunRules.ItemRoll("burst", "charm", 1, 0, 0));
        List<EmberRunRules.Grant> a = EmberRunRules.signatureGrants("q01", in, base), b = EmberRunRules.signatureGrants("q01", in, base);
        assertEquals(a.toString(), b.toString());
        assertEquals("sig_mark", a.get(0).key);
        assertTrue(EmberRunRules.signatureGrants("q08", in, base).isEmpty());
        assertEquals("sig_mark", EmberRunRules.signatureGrants("q07", in, base).get(0).key); // stage 3: Q07 is a signature map
        int stamps = 0;
        for (int i = 0; i < 4000; i++) {
            in.runId = "r" + i;
            for (EmberRunRules.Grant x : EmberRunRules.signatureGrants("q01", in, base)) if (x.kind == EmberRunRules.Kind.SIG) { stamps++; assertTrue(x.id.endsWith("/L02")); }
        }
        assertEquals(0.12, stamps / 4000.0, 0.02);
        // T0 base items never carry one
        EmberRunRules.Grant t0 = new EmberRunRules.Grant("base_item", EmberRunRules.Kind.ITEM, "u2", 1, new EmberRunRules.ItemRoll("none", "charm", 0, 0, 0));
        for (int i = 0; i < 200; i++) { in.runId = "z" + i; assertEquals(1, EmberRunRules.signatureGrants("q01", in, t0).size()); }
    }

    @Test
    public void burstReworkKeepsTheHammerOffTheTelegraphCost_D183() {
        // D183: 守炉重锤 pays only on boss damage now — its old 被预警招打中 +3% stacked with 炉心护符 / 门楼余烬 into a −3～−4 pp
        // net loss at low dodge (DESIGN-ember-mainline-unlocks §12); the charm L03 keeps its telegraph cost (it pays when hit)
        java.util.Map<String, Double> m = EmberSignature.byId("L04").mods;
        assertFalse(m.containsKey("taken_tele"));
        assertEquals(0.985, m.get("dmg_boss"), 1e-9);
        assertEquals(1.0, m.get("dodge_burst"), 1e-9);
        assertEquals(1.03, EmberSignature.byId("L03").mods.get("taken_tele"), 1e-9);
    }

    @Test
    public void stage3ChargeKey_D184() {
        // L13 炉锁巨锤: skill_charge is additive with default 0 (absent = no wind-up) and two sources keep the larger
        assertEquals(0.0, EmberGrowth.Mods.NONE.get("skill_charge"), 1e-9);
        java.util.Map<String, Double> l13 = EmberSignature.byId("L13").mods;
        EmberGrowth.Mods m = EmberGrowth.Mods.combine(Arrays.asList(l13, l13));
        assertEquals(l13.get("skill_charge"), m.get("skill_charge"), 1e-9);
        assertTrue(l13.get("skill_charge") > 0);
        assertEquals(1.0, EmberSignature.byId("L15").mods.get("burn_ticks"), 1e-9);
    }

    @Test
    public void attuneAlternates_D184() {
        // 签名调律: exactly L01 / L02 / L06 / L08 / L10 / L11 / L12; none for L03 / L04 / L05 / L07 / L09 / L13–L15
        assertEquals(Arrays.asList("L01", "L02", "L06", "L08", "L10", "L11", "L12"), new java.util.ArrayList<String>(EmberSignature.ALTS.keySet()));
        for (String id : new String[]{"L03", "L04", "L05", "L07", "L09", "L13", "L14", "L15"}) assertNull(id, EmberSignature.alt(EmberSignature.byId(id)));
        for (String id : EmberSignature.ALTS.keySet()) {
            EmberSignature.Def d = EmberSignature.byId(id);
            java.util.Map<String, Double> a = EmberSignature.alt(d).mods;
            assertFalse(a.equals(d.mods));
            assertTrue(EmberSignature.modsOf(d, true) == a && EmberSignature.modsOf(d, false) == d.mods);
            assertEquals(d.bad, EmberSignature.badOf(d, false));
            // same benefit: every additive (behaviour) key of the original is kept unchanged
            for (java.util.Map.Entry<String, Double> e : d.mods.entrySet())
                if (EmberGrowth.ADD.contains(e.getKey())) assertEquals(id + " " + e.getKey(), e.getValue(), a.get(e.getKey()));
            assertFalse(EmberSignature.alt(d).bad.startsWith("@"));
        }
        // the 'all boss damage' cost → 'telegraph only' swaps are roughly doubled (§11.3: same % was +3～+5.6 pp)
        assertEquals(1.04, EmberSignature.byId("L06").mods.get("taken_boss"), 1e-9);
        assertTrue(EmberSignature.alt(EmberSignature.byId("L06")).mods.get("taken_tele") >= 1.08);
        assertTrue(EmberSignature.alt(EmberSignature.byId("L11")).mods.get("taken_tele") >= 1.10);
        assertFalse(EmberSignature.alt(EmberSignature.byId("L06")).mods.containsKey("taken_boss"));
        // precheck
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        assertNull(EmberSignature.attuneCheck(l01, true, true, false, 10));
        assertNotNull(EmberSignature.attuneCheck(l01, true, true, false, 9));    // insignia short
        assertNull(EmberSignature.attuneCheck(l01, true, true, true, 0));        // unlocked: switching is free
        assertNotNull(EmberSignature.attuneCheck(l01, false, true, true, 99));   // Q07 not cleared
        assertNotNull(EmberSignature.attuneCheck(l01, true, false, false, 99));  // its own map not cleared
        assertNotNull(EmberSignature.attuneCheck(EmberSignature.byId("L05"), true, true, false, 99)); // no alternate
        assertNotNull(EmberSignature.attuneCheck(null, true, true, false, 99));
        assertEquals("q07", EmberSignature.ALT_UNLOCK);
        assertEquals(10, EmberSignature.ALT_MARKS);
    }

    @Test
    public void signatureTextsAreFilled() {
        for (EmberSignature.Def d : EmberSignature.DEFS) {
            assertFalse(d.id, d.good.startsWith("@") || d.bad.startsWith("@") || d.good.isEmpty() || d.bad.isEmpty());
        }
    }
}
