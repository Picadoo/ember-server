package town.sunshine.corerpg;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/** Pure retention / store / slotName rules for 1.62 inventory snapshots. */
public class InvSnapRulesTest {

    private static InvSnapRules.Snap s(String id, long at) { return new InvSnapRules.Snap(id, at); }

    @Test public void pruneKeepsNewestKeepLast() {
        long now = 1_700_000_000_000L; // fixed
        List<InvSnapRules.Snap> all = new ArrayList<InvSnapRules.Snap>();
        for (int i = 0; i < 60; i++) all.add(s("n" + i, now - i * 60_000L)); // 60 snaps within an hour
        List<String> del = InvSnapRules.prune(all, now, 50, 30);
        assertEquals(10, del.size());
        // oldest 10 ids n50..n59
        for (int i = 50; i < 60; i++) assertTrue(del.contains("n" + i));
        for (int i = 0; i < 50; i++) assertFalse(del.contains("n" + i));
    }

    @Test public void pruneKeepsDailyEvenBeyondKeepLast() {
        long day = InvSnapRules.DAY;
        long now = 10L * day; // aligned-ish
        List<InvSnapRules.Snap> all = new ArrayList<InvSnapRules.Snap>();
        // 60 newest within today
        for (int i = 0; i < 60; i++) all.add(s("today" + i, now - i * 1000L));
        // one snap per day for 30 days ago (outside keepLast of 50)
        for (int d = 1; d <= 35; d++) all.add(s("day" + d, now - d * day - 1000L));
        List<String> del = InvSnapRules.prune(all, now, 50, 30);
        Set<String> gone = new HashSet<String>(del);
        // days 1..29 (and 0=today already in keepLast) should be kept by daily rule; day 30..35 drop
        for (int d = 1; d < 30; d++) assertFalse("should keep day" + d, gone.contains("day" + d));
        for (int d = 30; d <= 35; d++) assertTrue("should drop day" + d, gone.contains("day" + d));
        // oldest today* beyond keepLast get dropped unless they are the day's newest (today0 is newest)
        assertTrue(gone.contains("today59") || gone.size() > 0);
    }

    @Test public void storeSkipsUnchangedAutomatic() {
        assertFalse(InvSnapRules.store("login", true));
        assertFalse(InvSnapRules.store("periodic", true));
        assertFalse(InvSnapRules.store("logout", true));
        assertTrue(InvSnapRules.store("login", false));
        assertTrue(InvSnapRules.store("manual", true));
        assertTrue(InvSnapRules.store("pre-restore", true));
        assertTrue(InvSnapRules.store("death", true));
    }

    @Test public void slotNameLabels() {
        assertEquals("快捷栏1", InvSnapRules.slotName(0));
        assertEquals("快捷栏9", InvSnapRules.slotName(8));
        assertEquals("背包1", InvSnapRules.slotName(9));
        assertEquals("背包27", InvSnapRules.slotName(35));
        assertEquals("靴子", InvSnapRules.slotName(36));
        assertEquals("护腿", InvSnapRules.slotName(37));
        assertEquals("胸甲", InvSnapRules.slotName(38));
        assertEquals("头盔", InvSnapRules.slotName(39));
        assertEquals("副手", InvSnapRules.slotName(40));
        assertEquals("?", InvSnapRules.slotName(-1));
        assertEquals("槽41", InvSnapRules.slotName(41));
    }

    @Test public void dayUsesCstOffset() {
        // 2020-01-01 00:00 UTC = 08:00 CST → same calendar day as 2020-01-01 CST
        long utcMidnight = 1577836800000L;
        assertEquals(InvSnapRules.day(utcMidnight), InvSnapRules.day(utcMidnight + 7 * 3600000L));
        // 16:00 UTC = next CST day (00:00 next day)
        assertEquals(InvSnapRules.day(utcMidnight) + 1, InvSnapRules.day(utcMidnight + 16 * 3600000L));
    }

    @Test public void stripOnRestoreVaultAndTickets() {
        java.util.Set<String> wl = new java.util.HashSet<String>(java.util.Arrays.asList(
                "mat_ember_shard", "mat_ember_v1_blank", "ember_fest_coin_gq26"));
        java.util.Set<String> extra = java.util.Collections.singleton("ember_gacha_ticket");
        assertTrue(InvSnapRules.stripOnRestore("mat_ember_shard", wl, extra));
        assertTrue(InvSnapRules.stripOnRestore("ember_fest_coin_gq26", wl, extra));
        assertTrue(InvSnapRules.stripOnRestore("ember_gacha_ticket", wl, extra));
        assertTrue(InvSnapRules.stripOnRestore("ember_gacha_ticket", wl, null)); // default ticket id
        assertFalse(InvSnapRules.stripOnRestore("some_sword", wl, extra));
        assertFalse(InvSnapRules.stripOnRestore(null, wl, extra));
        assertFalse(InvSnapRules.stripOnRestore("", wl, extra));
        assertEquals(3, InvSnapRules.countStrip(java.util.Arrays.asList(
                "mat_ember_shard", "dirt", "ember_gacha_ticket", "ember_fest_coin_gq26", "other"), wl, extra));
    }

    // ---------------- 1.64.1 net-out (review round 2 #1 repro numbers: 30 shards + 2 tickets)

    @Test public void depositAfterSnapshotIsNettedOut() {
        // snapshot: 30 shards in the backpack, warehouse 22; then 一键存入 30 → warehouse 52
        assertEquals(30, InvSnapRules.vaultDeduct(30, 22, 52, 30, 0, true));
        // nothing moved: restore everything (real loss case)
        assertEquals(0, InvSnapRules.vaultDeduct(30, 22, 22, 0, 0, true));
    }

    @Test public void depositThenSpendFromWarehouseStillNetted() {
        // stash 30 then a forge took 30 from the warehouse: balance back to 22, but the stash log still says 30 left
        assertEquals(30, InvSnapRules.vaultDeduct(30, 22, 22, 30, 0, true));
    }

    @Test public void spentStraightFromBackpackNetted() {
        assertEquals(18, InvSnapRules.vaultDeduct(30, 22, 22, 0, 18, true));
        assertEquals(30, InvSnapRules.vaultDeduct(30, 22, 40, 10, 18, true)); // capped at the snapshot amount
    }

    @Test public void pickupsIntoWarehouseAreHeldBackConservatively() {
        // warehouse grew by 5 from pickups (not from the backpack): held back anyway (never a dupe, admin sees why)
        assertEquals(5, InvSnapRules.vaultDeduct(30, 22, 27, 0, 0, true));
        // warehouse shrank (withdrawn): nothing extra held back
        assertEquals(0, InvSnapRules.vaultDeduct(30, 22, 10, 0, 0, true));
    }

    @Test public void oldSnapshotOrUnreadableLogHoldsEverythingBack() {
        assertEquals(30, InvSnapRules.vaultDeduct(30, -1, 52, 0, 0, true));
        assertEquals(30, InvSnapRules.vaultDeduct(30, 22, 22, 0, 0, false));
        assertEquals(0, InvSnapRules.vaultDeduct(0, -1, 52, 30, 0, true));
    }

    @Test public void ticketsRedeemedAfterSnapshotNetted() {
        assertEquals(2, InvSnapRules.ticketDeduct(2, 2, true));
        assertEquals(1, InvSnapRules.ticketDeduct(3, 1, true));
        assertEquals(0, InvSnapRules.ticketDeduct(2, 0, true));
        assertEquals(2, InvSnapRules.ticketDeduct(2, 5, true));
        assertEquals(2, InvSnapRules.ticketDeduct(2, 0, false));
    }

    @Test public void takeFromStacksLastFirst() {
        int[] a = {16, 10, 4};
        assertEquals(12, InvSnapRules.takeFromStacks(a, 12));
        assertArrayEquals(new int[]{16, 2, 0}, a);
        int[] b = {5};
        assertEquals(5, InvSnapRules.takeFromStacks(b, 9));
        assertArrayEquals(new int[]{0}, b);
    }

    @Test public void netLineReadable() {
        assertTrue(InvSnapRules.netLine("余烬碎片", 30, 30, "快照后一键存入仓库 30").contains("快照 30 → 恢复 0"));
        assertTrue(InvSnapRules.vaultWhy(22, 52, 30, 0, true).contains("一键存入仓库 30"));
        assertTrue(InvSnapRules.vaultWhy(-1, 52, 30, 0, true).contains("旧快照"));
    }
}
