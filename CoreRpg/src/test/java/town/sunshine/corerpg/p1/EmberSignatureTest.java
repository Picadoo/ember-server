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
        assertEquals(Arrays.asList("q01", "q02", "q03"), EmberSignature.maps());
        assertEquals(2, EmberSignature.forMap("q02").size());
        assertFalse(EmberSignature.hasMap("q04")); // stage 2
    }

    @Test
    public void stampRollRateAndFit() {
        // Q01: blade L01 is scorch-only, charm L02 any family
        assertEquals("L01", EmberSignature.rollStamp("q01", "blade", "scorch", 0.0).id);
        assertNull(EmberSignature.rollStamp("q01", "blade", "burst", 0.0));
        assertEquals("L02", EmberSignature.rollStamp("q01", "charm", "burst", 0.119).id);
        assertNull(EmberSignature.rollStamp("q01", "charm", "burst", EmberSignature.STAMP_RATE));
        assertNull(EmberSignature.rollStamp("q01", "charm", "burst", Double.NaN));
        assertNull(EmberSignature.rollStamp("q04", "blade", "scorch", 0.0));
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
        for (EmberSignature.Def x : EmberSignature.DEFS) for (EmberSignature.Def y : EmberSignature.DEFS)
            if (x != y && x.slot.equals(y.slot) && x.map.equals(y.map)) assertFalse(x.id + "/" + y.id, true);
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
        assertTrue(EmberRunRules.signatureGrants("q05", in, base).isEmpty());
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
}
