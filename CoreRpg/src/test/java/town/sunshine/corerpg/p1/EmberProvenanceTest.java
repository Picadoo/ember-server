package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * D245 (ARCH S4 · item provenance): {@link EmberItemData.Origin} codec, signature compatibility of older items, copies keep
 * it, the DB column format round-trips (what the gear library / dismantle-undo / delivery / audit readers rebuild stacks
 * from), and every ITEM reward key the code writes resolves to a real REG source row.
 */
public class EmberProvenanceTest {

    private static final byte[] KEY = "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
    private static final long T = 1791200000000L; // 2026-10-06 (ms)

    private static EmberItemData drop() {
        EmberItemData d = new EmberItemData("0123456789abcdef0123456789abcdef", "ember_v1_scorch_blade_t2", "scorch", "blade", 2, 1, 2, 0, 0,
                true, "drop", EmberItemData.DATA_VERSION, 0, 0, 0, 0, 0);
        return d.withOrigin(EmberProvenance.forReward("base_item", "q05-muw0gxnw-nxd", T));
    }

    @Test public void itemsWithoutOriginKeepTheirExactCanonicalAndSignature() {
        EmberItemData old = new EmberItemData("0123456789abcdef0123456789abcdef", "ember_v1_scorch_blade_t2", "scorch", "blade", 2, 1, 2, 3, 0,
                true, "drop", 2, 4, 52, 1, 7, 2);
        // D208 v2 shape, byte for byte (an item signed by 1.65.42–1.65.69 must still verify)
        assertEquals("v2|0123456789abcdef0123456789abcdef|ember_v1_scorch_blade_t2|scorch|blade|2|1|2|3|0|1|drop|4|52|1|7|2", old.canonical());
        assertFalse(old.origin.present());
        assertEquals("", old.origin.packed());
        Map<String, Object> m = old.toMap();
        assertFalse(m.containsKey("om") || m.containsKey("os") || m.containsKey("or") || m.containsKey("ot"));
        EmberItemData back = EmberItemData.fromMap(m);
        assertEquals(old, back);
        assertTrue(back.verify(KEY, old.sign(KEY)));
        EmberItemData v1 = new EmberItemData("0123456789abcdef0123456789abcdef", "ember_v1_scorch_blade_t2", "scorch", "blade", 2, 1, 2, 3, 0,
                true, "drop", 1, 4);
        assertEquals("v1|0123456789abcdef0123456789abcdef|ember_v1_scorch_blade_t2|scorch|blade|2|1|2|3|0|1|drop|4", v1.canonical());
        assertNull(v1.validate());
    }

    @Test public void originRoundTripsThroughNbtAndIsSigned() {
        EmberItemData d = drop();
        assertNull(d.validate());
        assertEquals("q05|S01|q05-muw0gxnw-nxd|" + T / 1000, d.origin.packed());
        assertTrue(d.canonical().endsWith("|o:q05|S01|q05-muw0gxnw-nxd|" + T / 1000));
        Map<String, Object> m = d.toMap();
        assertEquals("q05", m.get("om")); assertEquals("S01", m.get("os")); assertEquals("q05-muw0gxnw-nxd", m.get("or"));
        assertEquals((int) (T / 1000), m.get("ot"));
        EmberItemData back = EmberItemData.fromMap(m);
        assertEquals(d, back);
        assertEquals(d.origin, back.origin);
        String sig = d.sign(KEY);
        assertTrue(back.verify(KEY, sig));
        // tamper: another map, or the provenance stripped, breaks the signature
        m.put("om", "q07");
        assertFalse(EmberItemData.fromMap(m).verify(KEY, sig));
        m.remove("om"); m.remove("os"); m.remove("or"); m.remove("ot");
        assertFalse(EmberItemData.fromMap(m).verify(KEY, sig));
    }

    @Test public void copiesKeepTheOrigin() {
        EmberItemData d = drop();
        assertEquals(d.origin, d.withRev(5).origin);
        assertEquals(d.origin, d.withItemKeys(31, 2, 7, 3).origin);
        assertEquals(d.origin, d.withAffix(42).origin);
        assertEquals(d.origin, d.withSig(4).origin);
        EmberItemData up = EmberUpgradeRules.copy(d, 3, 2, 2, 4, 0, true); // forge enhance / upgrade / refine all go through copy
        assertEquals(d.origin, up.origin);
        assertEquals(3, up.tier);
        assertEquals(d.origin, EmberItemKeys.fold(new town.sunshine.corerpg.PlayerData(), d.withRev(1)).origin);
    }

    @Test public void dbColumnFormatRoundTrips() {
        // cr_p1_item.origin is Origin.packed(); lookupFull / loadByState (gear library take) / recentDismantles (undo) parse it
        EmberItemData d = drop();
        EmberItemData.Origin back = EmberItemData.Origin.parse(d.origin.packed());
        assertEquals(d.origin, back);
        assertSame(EmberItemData.Origin.NONE, EmberItemData.Origin.parse(""));
        assertSame(EmberItemData.Origin.NONE, EmberItemData.Origin.parse(null));
        assertNotNull(EmberItemData.Origin.parse("q01|S01").validate());
        assertNotNull(EmberItemData.Origin.parse("q01|S01|r|x").validate());
        // a row rebuilt from the DB (same fields + packed origin) is the same signed data as the NBT copy
        EmberItemData row = new EmberItemData(d.uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft, d.enhance, d.pity, d.bound,
                d.source, d.version, d.rev, d.affix, d.afPity, d.sigCode, d.rerollN, EmberItemData.Origin.parse(d.origin.packed()));
        assertEquals(d.canonical(), row.canonical());
        assertTrue(row.verify(KEY, d.sign(KEY)));
    }

    @Test public void validation() {
        EmberItemData d = drop();
        EmberItemData v1 = new EmberItemData(d.uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft, d.enhance, d.pity, d.bound,
                d.source, 1, 0, 0, 0, 0, 0, d.origin);
        assertEquals("v1 carries origin", v1.validate());
        assertNotNull(new EmberItemData(d.uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft, d.enhance, d.pity, d.bound,
                d.source, 2, 0, 0, 0, 0, 0, EmberItemData.Origin.parse("Q 5|S01|x|1")).validate());
        assertNotNull(EmberItemData.Origin.parse("q05|S1|x|1").validate());
        assertNotNull(EmberItemData.Origin.parse("q05|S01|a b|1").validate());
        assertNotNull(EmberItemData.Origin.parse("q05|S01|x|-1").validate());
        // of() normalises instead of failing
        EmberItemData.Origin o = EmberItemData.Origin.of("Q05 A2!", "nope", "run id with spaces|and|bars-and-a-very-long-tail-0123456789abcdef", -5);
        assertNull(o.validate());
        assertEquals("q05a2", o.map); assertEquals("X00", o.src); assertEquals(0L, o.at);
        assertTrue(o.run.length() <= 48 && !o.run.contains("|"));
    }

    @Test public void rewardSourcesResolve() {
        assertEquals("q03|S01", head(EmberProvenance.forReward("base_item", "q03-muvx0jjf-ia4", T)));
        assertEquals("q03c|S01", head(EmberProvenance.forReward("base_item", "q03c-muvx0jjf-ia4", T)));
        assertEquals("q05a2|S13", head(EmberProvenance.forReward("base_item", "q05a2-muvx0jjf-ia4", T)));
        assertEquals("q02|S34", head(EmberProvenance.forReward("extra_chest_item", "q02-muvx0jjf-ia4", T)));
        assertEquals("r01|S12", head(EmberProvenance.forReward("raid_item", "r01-muvx0jjf-ia4", T)));
        assertEquals("q01|S06", head(EmberProvenance.forReward("fc_q01_charm_item", "q01-muvx0jjf-ia4", T)));
        assertEquals("starter|S35", head(EmberProvenance.forReward("starter_blade", "starter", T)));
        assertEquals("forge|S28", head(EmberProvenance.forRedeem("redeem:0123abcd:muw0gx:1z", T)));
        assertEquals("admin|X03", head(EmberProvenance.forAdmin("givedup", T)));
        assertEquals("q09|X00", head(EmberProvenance.forReward("mystery_item", "q09-a-b", T)));
        // deterministic per ledger row: a re-delivery of the same reward builds the same signed data
        assertEquals(EmberProvenance.forReward("base_item", "q03-a-b", T), EmberProvenance.forReward("base_item", "q03-a-b", T));
        for (String src : new String[]{"S01", "S06", "S12", "S13", "S28", "S34", "S35"}) {
            EmberEconomy.Row r = EmberEconomy.byId(src);
            assertNotNull(src, r);
            assertTrue(src + " pays GEAR", r.accounts.contains(EmberEconomy.Account.GEAR));
        }
    }

    /** every ITEM reward key literal the run code writes (item(in, "…") / raidItem / choice _item / starter_) is registered */
    @Test public void everyItemRewardKeyInCodeHasASourceRow() throws Exception {
        File dir = new File("src/main/java/town/sunshine/corerpg/p1");
        Set<String> keys = new LinkedHashSet<String>();
        Pattern lit = Pattern.compile("item\\(in, \"([a-z_]+)\"\\)");
        for (File f : dir.listFiles()) {
            if (!f.getName().endsWith(".java")) continue;
            String src = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            Matcher m = lit.matcher(src);
            while (m.find()) keys.add(m.group(1));
        }
        assertTrue("found base_item / extra_chest_item: " + keys, keys.contains("base_item") && keys.contains("extra_chest_item"));
        for (String k : keys) assertNotEquals(k, EmberProvenance.SRC_UNKNOWN, EmberProvenance.itemSource(k, "q01-a-b"));
        assertEquals("S12", EmberProvenance.itemSource(EmberRaidService.G_ITEM, "r02-a-b"));
        assertEquals("S06", EmberProvenance.itemSource("fc_q04_blade_item", "q04-a-b")); // choiceItem: _choice → _item
        assertEquals("S06", EmberProvenance.itemSource("fc_q02_piece_item", "q02-a-b"));
        assertEquals("S35", EmberProvenance.itemSource("starter_charm", "starter"));
    }

    private static String head(EmberItemData.Origin o) { assertNull(o.validate()); return o.map + "|" + o.src; }
}
