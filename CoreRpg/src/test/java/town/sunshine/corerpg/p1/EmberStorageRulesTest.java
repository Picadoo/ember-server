package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/** Pure 1.62 vault / gearlib decision table. */
public class EmberStorageRulesTest {

    private static String hex(String tag) {
        // 32 hex chars from a short tag (pad with zeros)
        StringBuilder b = new StringBuilder();
        for (char c : tag.toCharArray()) b.append(String.format("%02x", (int) c));
        while (b.length() < 32) b.append('0');
        return b.substring(0, 32);
    }

    private static EmberItemData drop(String tag, int tier, int quality, String fam, String slot) {
        String uid = hex(tag);
        String ni = EmberItemData.templateId(fam, slot, tier);
        return new EmberItemData(uid, ni, fam, slot, tier, quality, 0, 0, 0, true, "drop", 1, 1);
    }

    private static EmberStorageRules.Entry e(EmberItemData d, boolean locked, boolean fav, long at) {
        return new EmberStorageRules.Entry(d, locked, fav, at);
    }

    @Test public void fitsRespectsCap() {
        assertEquals(0, EmberStorageRules.fits(0, 0));
        assertEquals(0, EmberStorageRules.fits(0, -5));
        assertEquals(100, EmberStorageRules.fits(0, 100));
        assertEquals(EmberStorageRules.VAULT_CAP, EmberStorageRules.fits(0, EmberStorageRules.VAULT_CAP + 10));
        assertEquals(5, EmberStorageRules.fits(EmberStorageRules.VAULT_CAP - 5, 100));
        assertEquals(0, EmberStorageRules.fits(EmberStorageRules.VAULT_CAP, 1));
        assertEquals(0, EmberStorageRules.fits(EmberStorageRules.VAULT_CAP + 50, 1));
    }

    @Test public void autoStashModes() {
        assertFalse(EmberStorageRules.autoStash(false, EmberStorageRules.STASH_ALWAYS, 0, 4, false, true));
        assertFalse(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_OFF, 0, 4, false, true));
        assertFalse(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_ALWAYS, 0, 4, true, true)); // upgrade
        assertFalse(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_ALWAYS, 0, 4, false, false)); // no active
        assertTrue(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_ALWAYS, 20, 4, false, true));
        assertTrue(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_LOW, 0, 4, false, true));
        assertTrue(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_LOW, 3, 4, false, true));
        assertFalse(EmberStorageRules.autoStash(true, EmberStorageRules.STASH_LOW, 4, 4, false, true));
        assertEquals("总是", EmberStorageRules.stashModeName(EmberStorageRules.STASH_ALWAYS));
        assertEquals("关闭", EmberStorageRules.stashModeName(EmberStorageRules.STASH_OFF));
        assertEquals("背包快满时", EmberStorageRules.stashModeName(EmberStorageRules.STASH_LOW));
        assertEquals(EmberStorageRules.STASH_ALWAYS, EmberStorageRules.nextStashMode(EmberStorageRules.STASH_LOW));
        assertEquals(EmberStorageRules.STASH_OFF, EmberStorageRules.nextStashMode(EmberStorageRules.STASH_ALWAYS));
        assertEquals(EmberStorageRules.STASH_LOW, EmberStorageRules.nextStashMode(EmberStorageRules.STASH_OFF));
    }

    @Test public void filterAndPage() {
        List<EmberStorageRules.Entry> all = Arrays.asList(
                e(drop("a", 2, 0, "scorch", "blade"), false, false, 100),
                e(drop("b", 3, 3, "burst", "charm"), false, true, 200),
                e(drop("c", 1, 1, "scorch", "blade"), true, false, 300),
                e(drop("d", 3, 2, "sustain", "blade"), false, false, 150)
        );
        EmberStorageRules.Filter f = new EmberStorageRules.Filter();
        assertEquals(4, EmberStorageRules.view(all, f).size());
        f.family = "scorch";
        List<EmberStorageRules.Entry> v = EmberStorageRules.view(all, f);
        assertEquals(2, v.size());
        assertEquals(hex("c"), v.get(0).d.uid); // newest first by default
        assertEquals(hex("a"), v.get(1).d.uid);
        f.sort = 1; // tier high→low
        v = EmberStorageRules.view(all, f);
        assertEquals(hex("a"), v.get(0).d.uid); // T2 before T1
        assertEquals(1, EmberStorageRules.pages(2, 28));
        assertEquals(2, EmberStorageRules.pages(29, 28));
        List<EmberStorageRules.Entry> page0 = EmberStorageRules.page(v, 0, 1);
        assertEquals(1, page0.size());
        assertEquals(hex("a"), page0.get(0).d.uid);
        f.cycleFamily();
        assertEquals("burst", f.family);
    }

    @Test public void bulkDismantleSkipsLockedFavEquippedAndNonDrop() {
        EmberItemData ok = drop("ok", 2, 0, "scorch", "blade");
        EmberItemData locked = drop("lk", 2, 0, "scorch", "blade");
        EmberItemData fav = drop("fv", 2, 0, "scorch", "blade");
        EmberItemData eq = drop("eq", 2, 0, "scorch", "blade");
        EmberItemData t0 = drop("t0", 0, 0, "scorch", "blade");
        EmberItemData forged = new EmberItemData(hex("fg"), EmberItemData.templateId("scorch", "blade", 2), "scorch", "blade", 2, 0, 0, 0, 0, true, "forge", 1, 1);
        List<EmberStorageRules.Entry> view = Arrays.asList(
                e(ok, false, false, 1),
                e(locked, true, false, 2),
                e(fav, false, true, 3),
                e(eq, false, false, 4),
                e(t0, false, false, 5),
                e(forged, false, false, 6)
        );
        Set<String> equipped = new HashSet<String>(Collections.singleton(hex("eq")));
        List<EmberStorageRules.Entry> take = EmberStorageRules.bulkDismantle(view, equipped);
        assertEquals(1, take.size());
        assertEquals(hex("ok"), take.get(0).d.uid);
        assertEquals(2, EmberStorageRules.blanksOf(take));
    }

    @Test public void undoWindow() {
        long at = 1_000_000L;
        assertTrue(EmberStorageRules.undoOpen(at, at + 5 * 60_000L, 10));
        assertTrue(EmberStorageRules.undoOpen(at, at + 10 * 60_000L, 10));
        assertFalse(EmberStorageRules.undoOpen(at, at + 10 * 60_000L + 1, 10));
        assertFalse(EmberStorageRules.undoOpen(at, at - 1, 10));
        assertFalse(EmberStorageRules.undoOpen(0, at, 10));
        assertFalse(EmberStorageRules.undoOpen(at, at + 1, 0));
        assertEquals("5 分 00 秒", EmberStorageRules.left(at, at + 5 * 60_000L, 10));
        assertEquals("0 分 01 秒", EmberStorageRules.left(at, at + 10 * 60_000L - 1000L, 10));
        assertEquals("0 分 00 秒", EmberStorageRules.left(at, at + 11 * 60_000L, 10));
    }
}
