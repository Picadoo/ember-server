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
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, 2, 1, 2, 6, 0, true, "admin", 2, 0).validate());
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
}
