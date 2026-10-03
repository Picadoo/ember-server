package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/** 1.62 P1 storage: vault cap, auto-stash, library filter/sort/page, bulk dismantle exclusions, undo window. */
public class EmberStorageRulesTest {

    private static EmberItemData item(String fam, String slot, int tier, int q, int enh, String src) {
        return EmberItemData.create(fam, slot, tier, q, 0, enh, false, src);
    }

    @Test public void vaultCap() {
        assertEquals(10, EmberStorageRules.fits(0, 10));
        assertEquals(5, EmberStorageRules.fits(EmberStorageRules.VAULT_CAP - 5, 10));
        assertEquals(0, EmberStorageRules.fits(EmberStorageRules.VAULT_CAP, 10));
        assertEquals(0, EmberStorageRules.fits(0, -3));
    }

    @Test public void autoStashTable() {
        int LOW = EmberStorageRules.STASH_LOW, ALW = EmberStorageRules.STASH_ALWAYS, OFF = EmberStorageRules.STASH_OFF;
        assertTrue(EmberStorageRules.autoStash(true, LOW, 3, 4, false, true));
        assertFalse(EmberStorageRules.autoStash(true, LOW, 10, 4, false, true));
        assertTrue(EmberStorageRules.autoStash(true, ALW, 30, 4, false, true));
        assertFalse(EmberStorageRules.autoStash(true, OFF, 0, 4, false, true));
        assertFalse("upgrade goes to the backpack", EmberStorageRules.autoStash(true, ALW, 0, 4, true, true));
        assertFalse("no active piece: keep it to equip", EmberStorageRules.autoStash(true, ALW, 0, 4, false, false));
        assertFalse("no MySQL", EmberStorageRules.autoStash(false, ALW, 0, 4, false, true));
        assertEquals(ALW, EmberStorageRules.nextStashMode(LOW));
        assertEquals(OFF, EmberStorageRules.nextStashMode(ALW));
        assertEquals(LOW, EmberStorageRules.nextStashMode(OFF));
    }

    private static List<EmberStorageRules.Entry> lib() {
        List<EmberStorageRules.Entry> l = new ArrayList<EmberStorageRules.Entry>();
        l.add(new EmberStorageRules.Entry(item("burst", "blade", 1, 0, 0, "drop"), false, false, 100));
        l.add(new EmberStorageRules.Entry(item("scorch", "charm", 3, 2, 5, "drop"), false, false, 300));
        l.add(new EmberStorageRules.Entry(item("burst", "charm", 2, 4, 2, "drop"), true, false, 200));
        l.add(new EmberStorageRules.Entry(item("sustain", "blade", 2, 1, 9, "drop"), false, true, 400));
        l.add(new EmberStorageRules.Entry(item("burst", "blade", 3, 3, 1, "craft"), false, false, 50));
        return l;
    }

    @Test public void filterSortPage() {
        EmberStorageRules.Filter f = new EmberStorageRules.Filter();
        List<EmberStorageRules.Entry> v = EmberStorageRules.view(lib(), f);
        assertEquals(5, v.size());
        assertEquals(400, v.get(0).storedAt); // newest first
        f.family = "burst";
        assertEquals(3, EmberStorageRules.view(lib(), f).size());
        f.slot = "blade";
        assertEquals(2, EmberStorageRules.view(lib(), f).size());
        f = new EmberStorageRules.Filter();
        f.sort = 1;
        assertEquals(3, EmberStorageRules.view(lib(), f).get(0).d.tier);
        f.sort = 2;
        assertEquals(4, EmberStorageRules.view(lib(), f).get(0).d.quality);
        f.sort = 3;
        assertEquals(9, EmberStorageRules.view(lib(), f).get(0).d.enhance);
        assertEquals(1, EmberStorageRules.pages(0, 45));
        assertEquals(3, EmberStorageRules.pages(91, 45));
        List<Integer> n = new ArrayList<Integer>();
        for (int i = 0; i < 91; i++) n.add(i);
        assertEquals(1, EmberStorageRules.page(n, 2, 45).size());
        assertEquals(1, EmberStorageRules.page(n, 99, 45).size()); // clamps to the last page
    }

    @Test public void bulkDismantleSkipsLockedFavEquippedAndRefused() {
        List<EmberStorageRules.Entry> l = lib();
        String equipped = l.get(0).d.uid;
        List<EmberStorageRules.Entry> b = EmberStorageRules.bulkDismantle(l, Collections.singleton(equipped));
        assertEquals(1, b.size()); // only the scorch charm: [0] equipped, [2] locked, [3] fav, [4] craft source
        assertSame(l.get(1), b.get(0));
        assertEquals(3, EmberStorageRules.blanksOf(b));
    }

    @Test public void undoWindow() {
        assertTrue(EmberStorageRules.undoOpen(1000, 1000 + 9 * 60000L, 10));
        assertFalse(EmberStorageRules.undoOpen(1000, 1000 + 10 * 60000L + 1, 10));
        assertFalse(EmberStorageRules.undoOpen(1000, 1000, 0));
        assertEquals("1 分 00 秒", EmberStorageRules.left(0, 9 * 60000L, 10));
    }

    @Test public void txnItemDefaultsToActive() {
        EmberItemData d = item("burst", "blade", 1, 0, 0, "drop");
        assertEquals("active", new EmberItemStore.TxnItem(d, null, "dismantled").expectState);
        assertEquals("stored", new EmberItemStore.TxnItem(d, null, "dismantled", "stored").expectState);
    }
}
