package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

import java.util.List;

import static org.junit.Assert.*;

/** D208 (ARCH S1-4 · REG §4-4): item keys on the item (v2) vs the legacy PlayerData counters (v1), fold and clear. */
public class EmberItemKeysTest {

    private static EmberItemData v1() {
        EmberItemData d = EmberItemData.create("burst", "blade", 2, 1, 2, 6, true, "admin");
        return new EmberItemData(d.uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft, d.enhance, d.pity, d.bound, d.source, 1, 0);
    }

    private static PlayerData legacy(String uid) {
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberGrowthService.C_AF + uid, "all", 52);
        pd.addPeriodCount(EmberGrowthService.C_AFP + uid, "all", 4);
        pd.addPeriodCount(EmberSignature.C_SIG + uid, "all", 3);
        pd.addPeriodCount(EmberPayRules.C_RRN + uid, "all", 7);
        return pd;
    }

    @Test public void v1ReadsTheLegacyCounters() {
        EmberItemData it = v1();
        PlayerData pd = legacy(it.uid);
        assertEquals(52, EmberItemKeys.affix(pd, it));
        assertEquals(4, EmberItemKeys.afPity(pd, it));
        assertEquals(3, EmberItemKeys.sig(pd, it));
        assertEquals(7, EmberItemKeys.rerollN(pd, it));
        assertTrue(EmberItemKeys.hasLegacy(pd, it.uid));
        assertEquals(0, EmberItemKeys.affix(null, it));
        assertEquals(0, EmberItemKeys.sig(new PlayerData(), it));
    }

    @Test public void v2PrefersTheItemAndIgnoresStaleCounters() {
        EmberItemData it = v1().withItemKeys(61, 0, 5, 9);
        PlayerData pd = legacy(it.uid); // stale counters must not override the item (not even a legitimate 0 pity)
        assertEquals(61, EmberItemKeys.affix(pd, it));
        assertEquals(0, EmberItemKeys.afPity(pd, it));
        assertEquals(5, EmberItemKeys.sig(pd, it));
        assertEquals(9, EmberItemKeys.rerollN(pd, it));
        assertEquals(61, EmberItemKeys.affix(null, it));
        EmberItemData empty = v1().withItemKeys(0, 0, 0, 0);
        assertEquals(0, EmberItemKeys.affix(pd, empty)); // v2 with 0 = empty slot, not "look at the counter"
        assertEquals(0, EmberItemKeys.sig(pd, empty));
    }

    @Test public void foldThenClearMovesEverythingOnce() {
        EmberItemData it = v1();
        PlayerData pd = legacy(it.uid);
        pd.addPeriodCount(EmberPayRules.C_RRO + it.uid, "all", 15); // a pending legacy roll is NOT an item key
        EmberItemData f = EmberItemKeys.fold(pd, it);
        assertEquals(2, f.version);
        assertNull(f.validate());
        assertEquals(52, f.affix); assertEquals(4, f.afPity); assertEquals(3, f.sigCode); assertEquals(7, f.rerollN);
        assertEquals(it.rev, f.rev);
        assertTrue(EmberItemKeys.clearLegacy(pd, it.uid));
        assertFalse(EmberItemKeys.hasLegacy(pd, it.uid));
        assertEquals(0, pd.periodCount(EmberGrowthService.C_AF + it.uid, "all"));
        assertEquals(0, pd.periodCount(EmberSignature.C_SIG + it.uid, "all"));
        assertEquals(15, pd.periodCount(EmberPayRules.C_RRO + it.uid, "all")); // recoverRolls owns it
        assertTrue(pd.periodCount(EmberGrowthService.C_SIGSEEN + EmberSignature.byCode(3).id, "all") > 0); // codex keeps 「获得过」
        assertFalse(EmberItemKeys.clearLegacy(pd, it.uid)); // idempotent
        // after the clear the folded item still reads the same (no double-apply, no loss)
        assertEquals(52, EmberItemKeys.affix(pd, f));
        assertEquals(3, EmberItemKeys.sig(pd, f));
        // a v2 piece is never folded again
        assertSame(f, EmberItemKeys.fold(legacy(it.uid), f));
    }

    @Test public void foldOfAPieceWithoutCountersIsZeroV2() {
        EmberItemData f = EmberItemKeys.fold(new PlayerData(), v1());
        assertEquals(2, f.version);
        assertEquals(0, f.affix + f.afPity + f.sigCode + f.rerollN);
    }

    @Test public void rerollRidsAreUniquePerAttempt() {
        String uid = v1().uid;
        assertEquals("afx:" + uid + ":3", EmberPayRules.rerollRid(uid, 3)); // legacy p4_rro_ requests (recoverRolls)
        String a = EmberPayRules.rerollRid(uid, 3, 1000L), b = EmberPayRules.rerollRid(uid, 3, 2000L);
        assertTrue(a.startsWith("afx:" + uid + ":3:"));
        assertNotEquals(a, b); // a refunded attempt never re-uses its request id
        assertTrue(a.length() <= 64);
        assertTrue(EmberPayRules.imprintRid(uid, 4, 1000L).startsWith("imp:" + uid + ":4:"));
        assertTrue(EmberPayRules.imprintRid(uid, 4, 1000L).length() <= 64);
    }

    @Test public void insigniaPriceRefundsAsSigmark() {
        EmberPay.Price p = EmberPay.Price.insignia(500, "q01", 3);
        List<EmberItemStore.Owed> o = p.owed("烙印退回");
        assertEquals(2, o.size());
        assertEquals("coin", o.get(0).kind);
        assertEquals(500, o.get(0).amount);
        assertEquals("sigmark", o.get(1).kind);
        assertEquals("q01", o.get(1).item);
        assertEquals(3, o.get(1).amount);
        assertTrue(p.json().contains("\"sigmark_q01\":3"));
        assertTrue(EmberPayRules.owed(null, 0, 0, 0, "nomap", 3, "x").isEmpty()); // only a real signature map
        assertTrue(EmberPayRules.sigItem("q01"));
        assertFalse(EmberPayRules.sigItem("../x"));
    }
}
