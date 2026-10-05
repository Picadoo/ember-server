package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** Trusted item identity: validation, NBT codec round-trip, HMAC signature and tamper detection. */
public class EmberItemDataTest {

    private static final byte[] KEY = "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

    private static EmberItemData sample() {
        return EmberItemData.create("burst", "blade", 2, 1, 2, 6, true, "admin");
    }

    @Test public void createDerivesTemplateAndFreshUid() {
        EmberItemData a = sample(), b = sample();
        assertEquals("ember_v1_burst_blade_t2", a.ni);
        assertNull(a.validate());
        assertNotEquals(a.uid, b.uid);
        assertTrue(a.uid.matches("[0-9a-f]{32}"));
        assertEquals(EmberItemData.DATA_VERSION, a.version);
        assertEquals(0, a.rev);
    }

    @Test public void t0IsFamilyNone() {
        EmberItemData d = EmberItemData.create("scorch", "charm", 0, 0, 0, 0, true, "quest");
        assertEquals("none", d.family);
        assertEquals("ember_v1_t0_charm", d.ni);
        assertNull(d.validate());
    }

    @Test public void codecRoundTrip() {
        EmberItemData d = sample();
        Map<String, Object> m = d.toMap();
        // NBT stores small ints as int; strings as strings
        EmberItemData back = EmberItemData.fromMap(m);
        assertEquals(d, back);
        assertEquals(d.canonical(), back.canonical());
    }

    @Test public void codecFromStringsAndMissing() {
        Map<String, Object> m = new HashMap<String, Object>(sample().toMap());
        m.put("tier", "2");
        assertNull(EmberItemData.fromMap(m).validate());
        m.remove("enh");
        assertNotNull(EmberItemData.fromMap(m).validate()); // enhance -1
    }

    @Test public void validationRejectsEveryBadField() {
        EmberItemData d = sample();
        assertNotNull(new EmberItemData("XYZ", d.ni, d.family, d.slot, 2, 1, 2, 6, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, "ring", 2, 1, 2, 6, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, "frost", d.slot, 2, 1, 2, 6, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 4, 1, 2, 6, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 4, 2, 6, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 4, 6, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 11, 0, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 6, 13, true, "admin", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 6, 0, true, "lore", 1, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 6, 0, true, "admin", 3, 0).validate()); // D208: v1 + v2 valid
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 6, 0, true, "admin", 0, 0).validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 6, 0, true, "admin", 1, -1).validate());
        // NI id must be the template of family/slot/tier: a T2 blade claiming T3 numbers is rejected
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 3, 1, 2, 6, 0, true, "admin", 1, 0).validate());
        // T0 must be family none and vice versa
        assertNotNull(new EmberItemData(d.uid, "ember_v1_t0_blade", "burst", "blade", 0, 0, 0, 0, 0, true, "admin", 1, 0).validate());
    }

    @Test public void signatureVerifies() {
        EmberItemData d = sample();
        String sig = d.sign(KEY);
        assertEquals(32, sig.length());
        assertTrue(d.verify(KEY, sig));
        assertTrue(EmberItemData.fromMap(d.toMap()).verify(KEY, sig));
    }

    @Test public void tamperingBreaksSignature() {
        EmberItemData d = sample();
        String sig = d.sign(KEY);
        Map<String, Object> m = new HashMap<String, Object>(d.toMap());
        m.put("enh", 10);
        assertFalse(EmberItemData.fromMap(m).verify(KEY, sig));
        m = new HashMap<String, Object>(d.toMap());
        m.put("q", 3);
        assertFalse(EmberItemData.fromMap(m).verify(KEY, sig));
        assertFalse(d.withRev(1).verify(KEY, sig));
        assertFalse(d.verify("another-key-another-key-another!!".getBytes(StandardCharsets.US_ASCII), sig));
        assertFalse(d.verify(KEY, null));
        assertFalse(d.verify(KEY, "short"));
    }

    @Test public void labels() {
        assertEquals("T2 烬爆刃｜成色精良｜精工4%｜+6", sample().shortLabel());
    }

    // ------------------------------------------------------------------ D208 (ARCH S1-4): item keys, data version 2

    /** the pre-1.65.42 signer, written out independently: HMAC-SHA256 of the string, first 128 bits hex */
    private static String hmac(String canonical) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(KEY, "HmacSHA256"));
            byte[] h = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) sb.append(String.format("%02x", h[i] & 0xff));
            return sb.toString();
        } catch (Exception e) { throw new AssertionError(e); }
    }

    private static EmberItemData v1() {
        EmberItemData d = sample();
        return new EmberItemData(d.uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft, d.enhance, d.pity, d.bound, d.source, 1, 3);
    }

    @Test public void v1KeepsItsExactCanonicalAndNbt() {
        EmberItemData a = v1();
        assertEquals("v1|" + a.uid + "|ember_v1_burst_blade_t2|burst|blade|2|1|2|6|0|1|admin|3", a.canonical());
        assertNull(a.validate());
        assertFalse(a.itemKeys());
        assertFalse(a.toMap().containsKey("af"));
        assertFalse(a.toMap().containsKey("sigc"));
        assertEquals(13, a.toMap().size());
    }

    @Test public void v1SignatureStillVerifiesAfterTheUpgrade() {
        EmberItemData a = v1();
        // the HMAC a pre-1.65.42 server wrote: over the v1 canonical string
        String old = hmac("v1|" + a.uid + "|ember_v1_burst_blade_t2|burst|blade|2|1|2|6|0|1|admin|3");
        assertTrue(a.verify(KEY, old));
        assertTrue(EmberItemData.fromMap(a.toMap()).verify(KEY, old)); // NBT without the v2 keys reads as 0 → same v1 data
    }

    @Test public void v1WithItemKeysIsInvalid() {
        EmberItemData a = v1();
        assertNotNull(new EmberItemData(a.uid, a.ni, a.family, a.slot, a.tier, a.quality, a.craft, a.enhance, a.pity, a.bound, a.source, 1, 0, 52, 0, 0, 0).validate());
        assertNotNull(new EmberItemData(a.uid, a.ni, a.family, a.slot, a.tier, a.quality, a.craft, a.enhance, a.pity, a.bound, a.source, 1, 0, 0, 0, 3, 0).validate());
        Map<String, Object> m = new HashMap<String, Object>(a.toMap());
        m.put("sigc", 3); // a v1 stack with a forged signature key: rejected
        assertNotNull(EmberItemData.fromMap(m).validate());
    }

    @Test public void v2CarriesAndSignsTheItemKeys() {
        EmberItemData b = v1().withItemKeys(52, 4, 3, 7).withRev(4);
        assertEquals(2, b.version);
        assertTrue(b.itemKeys());
        assertNull(b.validate());
        assertTrue(b.canonical().startsWith("v2|"));
        assertTrue(b.canonical().endsWith("|admin|4|52|4|3|7"));
        EmberItemData back = EmberItemData.fromMap(b.toMap());
        assertEquals(b, back);
        assertEquals(52, back.affix); assertEquals(4, back.afPity); assertEquals(3, back.sigCode); assertEquals(7, back.rerollN);
        String sig = b.sign(KEY);
        assertTrue(back.verify(KEY, sig));
        for (String k : new String[]{"af", "afp", "sigc", "rrn"}) { // every item key is covered by the HMAC
            Map<String, Object> m = new HashMap<String, Object>(b.toMap());
            m.put(k, ((Integer) m.get(k)) + 1);
            assertFalse(k, EmberItemData.fromMap(m).verify(KEY, sig));
        }
        Map<String, Object> m = new HashMap<String, Object>(b.toMap());
        m.put("ver", 1); m.remove("af"); m.remove("afp"); m.remove("sigc"); m.remove("rrn"); // downgrade to v1 to drop keys
        assertFalse(EmberItemData.fromMap(m).verify(KEY, sig));
        assertFalse(v1().verify(KEY, sig));
    }

    @Test public void v2BoundsAndKeepers() {
        EmberItemData b = sample().withItemKeys(52, 1, 3, 2);
        assertNotNull(sample().withItemKeys(-1, 0, 0, 0).validate());
        assertNotNull(sample().withItemKeys(0, -1, 0, 0).validate());
        assertNotNull(sample().withItemKeys(0, 0, EmberItemData.MAX_SIG + 1, 0).validate());
        assertNotNull(sample().withItemKeys(0, 0, 0, -1).validate());
        assertEquals(b.affix, b.withRev(9).affix);
        assertEquals(b.sigCode, b.withRev(9).sigCode);
        assertEquals(1, v1().withRev(9).version); // a library move keeps the version (no fold without the owner's data)
        assertEquals(61, b.withAffix(61).affix);
        assertEquals(3, b.withAffix(61).sigCode);
        assertEquals(5, b.withSig(5).sigCode);
        assertEquals(52, b.withSig(5).affix);
        // the forge copy keeps the keys and the version
        assertEquals(b.affix, EmberUpgradeRules.copy(b, b.tier, b.quality, b.craft, b.enhance + 1, b.pity, b.bound).affix);
    }
}
