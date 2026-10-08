package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * D319 · T2 hard prerequisite (b): invsnap restore guard against the six-slot migration (QA R3 @ 9bd4e430: a queued
 * restore runs at join + 40 ticks, before the migration at + 120, and puts the originals back on while the swap mark is
 * still on the player → ROLLED_FORWARD doubles the originals). Pure rules + the offline World model from
 * {@link EmberSixMigrationTest} + source wiring.
 */
public class EmberSixRestoreGuardTest {

    static final long DONE = 1760000000L; // World.nowSec()

    // ------------------------------------------------------------------ pure rules

    @Test public void neverMigratedAllowsEverything_switchOffUnchanged() {
        // the switch-off / never-enabled state: no p1-six/<uuid>.yml → always allow, whatever else
        for (boolean flag : new boolean[]{false, true})
            for (boolean j : new boolean[]{false, true})
                for (long done : new long[]{0, DONE})
                    for (long at : new long[]{0, 1, DONE * 1000L - 1, DONE * 1000L, Long.MAX_VALUE})
                        assertNull(EmberSixRestoreGuard.check(false, flag, j, done, at));
        // a record without flag and without journal (dropped journal / self-check revert: inventory back to pre-migration)
        assertNull(EmberSixRestoreGuard.check(true, false, false, 0, 5));
    }

    @Test public void journalPendingRefusesTemporarily() {
        for (long at : new long[]{0, DONE * 1000L, (DONE + 999) * 1000L}) {
            EmberSixRestoreGuard.Verdict v = EmberSixRestoreGuard.check(true, false, true, 0, at);
            assertNotNull(v);
            assertFalse("journal pending is resolved by the next hub visit → keep the queued restore", v.permanent);
            assertTrue(v.reason, v.reason.contains("journal"));
        }
    }

    @Test public void migratedSnapshotBeforeDoneRefusedForGoodAfterAllowed() {
        // before the migration finished (incl. the same second: seconds-resolution done_at) → permanent
        for (long at : new long[]{0, (DONE - 3600) * 1000L, DONE * 1000L, DONE * 1000L + 999}) {
            EmberSixRestoreGuard.Verdict v = EmberSixRestoreGuard.check(true, true, false, DONE, at);
            assertNotNull("snapshot @" + at, v);
            assertTrue(v.permanent);
            assertTrue(v.reason, v.reason.contains("复制"));
        }
        for (long at : new long[]{(DONE + 1) * 1000L, (DONE + 86400) * 1000L})
            assertNull(EmberSixRestoreGuard.check(true, true, false, DONE, at));
    }

    @Test public void oldRecordWithoutDoneAtIsRefusedConservatively() {
        for (long at : new long[]{0, DONE * 1000L, Long.MAX_VALUE}) {
            EmberSixRestoreGuard.Verdict v = EmberSixRestoreGuard.check(true, true, false, 0, at);
            assertNotNull(v);
            assertTrue(v.permanent);
            assertTrue(v.reason, v.reason.contains("done_at"));
        }
    }

    @Test public void reasonPointsToTheRunbook() {
        assertTrue(EmberSixRestoreGuard.MANUAL.startsWith("docs/ops/OPS-ember-six-slot-migration.md"));
    }

    // ------------------------------------------------------------------ the QA reproduction on the World model

    /** an invsnap snapshot: backpack + armor (the restore overwrites both; tags and the 待领 record are untouched) */
    static final class Snap {
        final String[] armor, pack; final long atMs;
        Snap(EmberSixMigrationTest.World w, long atMs) { armor = w.armor.clone(); pack = w.pack.clone(); this.atMs = atMs; }
        void restoreOnto(EmberSixMigrationTest.World w) { w.armor = armor.clone(); w.pack = pack.clone(); w.persistInventory0(); }
    }

    /** {@link EmberSixSlotService#restoreGuard} on the World: the record only (journal / flag / done_at), never the tags */
    static EmberSixRestoreGuard.Verdict guard(EmberSixMigrationTest.World w, Snap s) {
        EmberSixMigration.Record<String> r = w.disk;
        boolean exists = r.flag || r.journal != null || !r.stash.isEmpty() || !r.voided.isEmpty();
        return EmberSixRestoreGuard.check(exists, r.flag, r.journal != null, r.doneAt, s.atMs);
    }

    static boolean swapMarked(EmberSixMigrationTest.World w) { return w.swapMarkLive(); }

    /**
     * QA R3: the migration crashed after the swap (journal pending, swap mark on, new pieces worn); the admin queued a
     * restore to the pre-swap snapshot; at the next join the queued restore runs at tick 40, before the migration at
     * tick 120. Unguarded → the originals are doubled. Guarded → refused at 40 (queue kept), migration resumes at 120,
     * the next join refuses the same snapshot for good (dequeued); a post-migration snapshot is still restorable.
     */
    @Test public void queuedRestoreAtTick40BeforeMigrationAtTick120() {
        int pending = 0, doubledUnguarded = 0;
        for (int k = 0; k < 5; k++) {
            EmberSixMigrationTest.World probe = EmberSixMigrationTest.world(k);
            probe.migrate();
            int steps = probe.calls;
            for (int crash = 1; crash <= steps; crash++) {
                for (int restart = 0; restart < 2; restart++) {
                    for (int guarded = 0; guarded < 2; guarded++) {
                        EmberSixMigrationTest.World w = EmberSixMigrationTest.world(k);
                        List<String> f0 = w.foreign();
                        Snap pre = new Snap(w, (DONE - 3600) * 1000L); // a "join" snapshot taken before the migration
                        w.crashAt = crash;
                        try { w.migrate(); fail(); } catch (EmberSixMigrationTest.Crash expected) { }
                        if (restart == 0) w.softRestart(); else w.hardRestart(); // the join that runs the queued restore
                        if (w.disk.journal == null || !swapMarked(w)) break; // only the R3 window: journal pending + mark on
                        String why = "case " + k + " crash@" + crash + " restart " + restart;
                        // tick 40: the queued restore
                        boolean queued = true;
                        if (guarded == 1) {
                            EmberSixRestoreGuard.Verdict v = guard(w, pre);
                            assertNotNull(why + " refused at tick 40", v);
                            assertFalse(why + " temporary → stays queued", v.permanent);
                        } else {
                            pre.restoreOnto(w);
                            queued = false;
                        }
                        // tick 120: the migration resumes
                        EmberSixMigration.Outcome o = w.migrate();
                        if (o == EmberSixMigration.Outcome.JOURNAL_DROPPED) o = w.migrate();
                        if (guarded == 0) {
                            if (!f0.equals(w.foreign())) doubledUnguarded++;
                            continue;
                        }
                        pending++;
                        EmberSixMigrationTest.assertMigrated(w, f0, why + " → " + o);
                        assertTrue(why, w.disk.doneAt > 0);
                        // next join, tick 40: the same queued restore → refused for good, dequeued
                        w.softRestart();
                        EmberSixRestoreGuard.Verdict v = guard(w, pre);
                        assertNotNull(why + " pre-migration snapshot after the migration", v);
                        assertTrue(why + " permanent → dequeued", v.permanent);
                        assertTrue(queued);
                        // what the guard prevents: restoring it would put the originals on while they sit in 待领
                        EmberSixMigrationTest.World copy = EmberSixMigrationTest.world(k);
                        copy.disk = EmberSixMigrationTest.World.copy(w.disk);
                        copy.armor = w.armor.clone(); copy.pack = w.pack.clone(); copy.marks = new java.util.HashSet<String>(w.marks);
                        pre.restoreOnto(copy);
                        if (!w.disk.stash.isEmpty()) assertNotEquals(why + " unguarded post-migration restore doubles", f0, copy.foreign());
                        // a snapshot taken after the migration is restorable and changes nothing
                        Snap post = new Snap(w, (w.disk.doneAt + 60) * 1000L);
                        assertNull(why + " post-migration snapshot", guard(w, post));
                        post.restoreOnto(w);
                        EmberSixMigrationTest.assertMigrated(w, f0, why + " post restore");
                        assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
                    }
                }
            }
        }
        assertTrue("R3 window reached " + pending, pending >= 10);
        assertTrue("the model reproduces QA's doubling without the guard: " + doubledUnguarded, doubledUnguarded > 0);
    }

    /** the plain QA timing with no migration history: restore at 40, migration at 120 → allowed, and harmless */
    @Test public void neverMigratedQueuedRestoreBeforeFirstMigrationIsAllowedAndSafe() {
        for (int k = 0; k < 5; k++) {
            EmberSixMigrationTest.World w = EmberSixMigrationTest.world(k);
            Snap pre = new Snap(w, (DONE - 60) * 1000L);
            w.armor[0] = null; w.persistInventory0(); // something changed since the snapshot
            assertNull("case " + k, guard(w, pre));
            pre.restoreOnto(w);
            List<String> f0 = w.foreign();
            assertEquals(EmberSixMigration.Outcome.MIGRATED, w.migrate());
            EmberSixMigrationTest.assertMigrated(w, f0, "case " + k);
            assertEquals(DONE, w.disk.doneAt);
        }
    }

    /** D319 (a): an orphan swap tag (kill after the commit) does not lock the player out; only the snapshot time counts */
    @Test public void orphanTagDoesNotBlockPostMigrationRestore() {
        for (int k = 0; k < 5; k++) {
            EmberSixMigrationTest.World w = EmberSixMigrationTest.world(k);
            List<String> f0 = w.foreign();
            Snap pre = new Snap(w, (DONE - 60) * 1000L);
            w.crashAt = EmberSixMigrationTest.world(k).migrateCalls(); // kill at the post-commit save
            try { w.migrate(); fail(); } catch (EmberSixMigrationTest.Crash expected) { }
            w.hardRestart(); // orphan swap tag back from the player file
            assertTrue(w.swapMarkLive());
            Snap post = new Snap(w, (DONE + 30) * 1000L);
            assertNull("case " + k + " orphan tag ignored", guard(w, post));
            EmberSixRestoreGuard.Verdict v = guard(w, pre);
            assertNotNull(v);
            assertTrue(v.permanent);
            post.restoreOnto(w);
            assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate()); // and the visit tidies the tag
            assertFalse(w.swapMarkLive());
            EmberSixMigrationTest.assertMigrated(w, f0, "case " + k);
        }
    }

    @Test public void doneAtOnlyOnCommit() {
        EmberSixMigrationTest.World w = EmberSixMigrationTest.world(0);
        assertEquals(0, w.disk.doneAt);
        w.migrate();
        assertEquals(DONE, w.disk.doneAt);
        assertEquals(DONE, w.disk.copy().doneAt);
        assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
        assertEquals(DONE, w.disk.doneAt);
        // a self-check failure reverts: no flag, no done time
        EmberSixMigrationTest.World t = EmberSixMigrationTest.world(0);
        t.tamper = true;
        t.migrate();
        assertFalse(t.disk.flag);
        assertEquals(0, t.disk.doneAt);
    }

    // ------------------------------------------------------------------ wiring

    static String src(String p) throws Exception {
        return new String(Files.readAllBytes(Paths.get("src/main/java/town/sunshine/corerpg/" + p)), StandardCharsets.UTF_8);
    }

    static String body(String s, String sig) {
        int i = s.indexOf(sig);
        assertTrue(sig, i >= 0);
        int b = s.indexOf('{', i), d = 0;
        for (int j = b; j < s.length(); j++) {
            if (s.charAt(j) == '{') d++;
            else if (s.charAt(j) == '}' && --d == 0) return s.substring(b, j + 1);
        }
        throw new AssertionError(sig);
    }

    @Test public void wiredIntoImmediateAndQueuedRestore() throws Exception {
        String inv = src("InvSnapService.java");
        String restore = body(inv, "private void restore(final CommandSender admin, final Player p, final Snap s, final java.util.function.Consumer<String> done)");
        int g = restore.indexOf("EmberSixSlotService.restoreGuard(p, s.at)");
        assertTrue("restore() asks the guard", g > 0);
        assertTrue("before anything else (freeze / pre-restore snapshot / apply)", g < restore.indexOf("frozen") || restore.indexOf("frozen") < 0);
        assertTrue(g < restore.indexOf("apply("));
        assertTrue(restore.contains("done.accept(") && restore.indexOf("return;") > g);
        String pend = body(inv, "private void applyPending(final Player p, final String id)");
        int pg = pend.indexOf("EmberSixSlotService.restoreGuard(p, s.at)");
        assertTrue("queued restore re-checked when it runs", pg > 0 && pg < pend.indexOf("restore(Bukkit.getConsoleSender()"));
        assertTrue("permanent → dequeue", pend.contains("if (six.permanent) { setPending(u, null, null)"));
        assertTrue("temporary → kept", pend.contains("else pendingFailed(u, \"six-slot guard"));
        assertTrue(body(inv, "private void preview(").contains("restoreGuard"));
        assertTrue(body(inv, "private void refuseSix(").contains("EmberSixRestoreGuard.MANUAL"));
        // the QA timing this guards: invsnap at join + 40, the migration at join + 120
        assertTrue(body(inv, "public void onJoin(PlayerJoinEvent e)").contains("}, 40L);"));
        String six = src("p1/EmberSixSlotService.java");
        assertTrue(six.contains("() -> tryMigrate(p), 120L);"));
        // never migrated → null after one exists(), before any record read; tags never read (D319 (a))
        String rg = body(six, "public static EmberSixRestoreGuard.Verdict restoreGuard(Player p, long snapAtMs)");
        assertTrue(rg.indexOf("if (!svc.file(p.getUniqueId()).exists()) return null;") < rg.indexOf("svc.load("));
        assertFalse(rg.contains("getScoreboardTags"));
        // the flag and done_at are written in the same record save
        String mig = src("p1/EmberSixMigration.java");
        String commit = mig.substring(mig.indexOf("next.flag = true;"), mig.indexOf("port.save(next); // flag + done_at + pieces + 待领"));
        assertTrue(commit.contains("next.doneAt = port.nowSec();"));
        // orphan clean-up: ALREADY + no journal → drop marks + checked save
        String run = body(mig, "public Outcome run()");
        assertTrue(run.contains("if (rec.journal == null) { tidyOrphanMarks(); heal(port, rec); }"));
        String cm = body(mig, "private Outcome commit(Record<S> rec, Outcome done, String detail)");
        assertTrue("(c) save right after the unmark", cm.indexOf("port.apply(null, j.mark, false)") < cm.indexOf("port.persistInventory()"));
        String tidy = body(mig, "private void tidyOrphanMarks()");
        assertTrue(tidy.indexOf("port.dropSwapMarks()") < tidy.indexOf("port.persistInventory()"));
        assertTrue(body(six, "public boolean persistInventory()").contains("savePlayerFileChecked"));
        assertTrue(six.contains("r.doneAt = y.getLong(\"done_at\", 0L);"));
        assertTrue(six.contains("if (r.doneAt > 0) y.set(\"done_at\", r.doneAt);"));
    }

    @Test public void tagPrefixesMatchTheService() throws Exception {
        String six = src("p1/EmberSixSlotService.java");
        assertTrue(six.contains("MARK_SWAP = \"ember_six_m_\", MARK_CLAIM = \"ember_six_c_\""));
        assertTrue(body(six, "public int dropSwapMarks()").contains("t.startsWith(MARK_SWAP)"));
    }
}
