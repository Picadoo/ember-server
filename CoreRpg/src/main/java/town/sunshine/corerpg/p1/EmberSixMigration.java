package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * D318 六槽 T1 · 旧档迁移（spec 2026-10-08 §3.1 / §3.2），Bukkit-free so every crash point can be unit-tested
 * ({@code EmberSixMigrationTest}). The Bukkit side is {@link EmberSixSlotService}; {@code S} is the stack type
 * (ItemStack live, String in tests).
 *
 * <p>Order (crash-safe, at most one valid issue):
 * <ol>
 *   <li>flag {@code p1_six_mig} set → return (idempotent);</li>
 *   <li>a journal left by an earlier crash → {@link #reconcile};</li>
 *   <li>plan 4 pieces (charm family / tier / quality / craft, enhance 0, source migrate, bound, origin X04; no or T0 charm
 *       → 4 × T0) and <b>pre-check</b> H0/H/D/M/EHP bits (2-slot vs six-slot with the planned pieces) — mismatch → nothing
 *       happens, OP alert;</li>
 *   <li>build the stacks (missing NI template → nothing happens);</li>
 *   <li>save the journal (originals + issued data + swap mark) — PREPARED;</li>
 *   <li><b>swap</b>: the four slots and the swap mark on the player change in one step ({@link Port#apply}); save the
 *       player file;</li>
 *   <li><b>post-check</b> from the stacks actually worn — mismatch → revert (originals back, mark off), no DB row;</li>
 *   <li>DB remember the issued pieces; commit = flag 1 + originals into 待领 + journal cleared, one atomic record save.</li>
 * </ol>
 *
 * <p><b>Where the originals are</b> (T1 acceptance, residual risk A/B/C): the swap mark travels with the inventory in
 * the player file (live: a scoreboard tag), so a journal is resolved from the mark, never from whether the issued pieces
 * are still on the player (the player may have thrown, stored or lost them). No mark → this inventory never saw the swap,
 * the originals are wherever the player keeps them → the journal (a note) is dropped. Mark → the swap took the originals
 * off the player, the journal is their only copy → they go to 待领 in the commit (or back into their slots on a revert),
 * whatever happened to the issued pieces. A missing issued piece is re-issued under a <b>new</b> uid; the old uid is
 * written to the record's {@code voided} list first and then retired in the DB (state retired + trust cache), and the
 * list is re-sent on every join, so one piece never has two valid uids even when the async DB write is lost.
 *
 * <p><b>Persist before mark</b>: every record write that relies on the player's inventory being in some state (commit /
 * journal dropped / 待领 entry removed) comes <i>after</i> a successful player-file save of exactly that state.
 * {@link Port#persistInventory} reports success; a failed save never sets the flag or drops / clears anything, returns
 * {@link Outcome#SAVE_FAILED} (claim throws {@link SaveFailed}), and the next visit resumes.
 */
public final class EmberSixMigration<S> {

    public static final String FLAG = "p1_six_mig";
    public static final String ORIGIN_SRC = "X04";
    public static final String ORIGIN_MAP = "migrate";
    public static final String SOURCE = "migrate";

    public enum Outcome { ALREADY, MIGRATED, ROLLED_FORWARD, JOURNAL_DROPPED, CHECK_FAILED, NO_TEMPLATE, SAVE_FAILED, ERROR }

    /** the player file could not be written during a 待领 claim; nothing was marked, the entry is still owed */
    public static final class SaveFailed extends RuntimeException {
        public SaveFailed(String msg) { super(msg); }
    }

    /** One 待领 entry: an item that sat in an armor slot before the migration (or a re-issued piece with no free slot). */
    public static final class StashEntry<S> {
        public final String id;
        public final int slot;
        public final S item;
        public StashEntry(String id, int slot, S item) { this.id = id; this.slot = slot; this.item = item; }
    }

    /** PREPARED journal: what was in the slots and what is being issued (P1 order head, chest, legs, boots). */
    public static final class Journal<S> {
        public final List<S> originals;          // size 4, null = empty slot (or already moved to 待领 by a revert)
        public final List<EmberItemData> issued; // size 4
        public final long at;
        /** true once the post-check failed: a crash mid-revert finishes the revert, never the issue */
        public final boolean reverting;
        /** the swap mark this journal's swap puts on the player (stays the same when a piece is re-issued) */
        public final String mark;
        public Journal(List<S> originals, List<EmberItemData> issued, long at, boolean reverting, String mark) {
            this.originals = originals; this.issued = issued; this.at = at; this.reverting = reverting; this.mark = mark;
        }
        Journal<S> with(List<S> o, List<EmberItemData> iss, boolean rev) { return new Journal<S>(o, iss, at, rev, mark); }
    }

    /** The per-player record file (flag + journal + 待领 + voided uids). */
    public static final class Record<S> {
        public boolean flag;
        public Journal<S> journal;
        public final LinkedHashMap<String, StashEntry<S>> stash = new LinkedHashMap<String, StashEntry<S>>();
        public int seq;
        /** issued pieces whose uid was given up (re-issued / never handed out): retired in the DB, re-sent on join */
        public final List<EmberItemData> voided = new ArrayList<EmberItemData>();
        /** epoch seconds of the commit that set the flag (0 = not migrated); the invsnap restore guard compares snapshots to it */
        public long doneAt;
        public Record<S> copy() {
            Record<S> r = new Record<S>();
            r.flag = flag; r.journal = journal; r.seq = seq; r.stash.putAll(stash); r.voided.addAll(voided); r.doneAt = doneAt;
            return r;
        }
    }

    /** What the loadout looked like before (the 2-slot inputs). */
    public static final class Snapshot {
        public final EmberTables t;
        public final EmberItemData blade, charm;
        public final int level;
        public final double festHp, festDef;
        public Snapshot(EmberTables t, EmberItemData blade, EmberItemData charm, int level, double festHp, double festDef) {
            this.t = t; this.blade = blade; this.charm = charm; this.level = level; this.festHp = festHp; this.festDef = festDef;
        }
    }

    /** Side effects; each call may throw (crash injection in tests: an exception = the process dies at that point). */
    public interface Port<S> {
        Record<S> load();
        void save(Record<S> r);
        Snapshot snapshot();
        /** current armor stacks, P1 order */
        S armor(int i);
        void setArmor(int i, S stack);
        /**
         * set all four armor slots and put on ({@code marked}) / take off the swap mark, as one step (same tick: no player
         * save can see one without the other). The mark is saved with the inventory in the player file.
         */
        void apply(List<S> armor, String mark, boolean marked);
        /** the swap mark is on the player (live) */
        boolean marked(String mark);
        /**
         * take every swap mark (any journal's) off the live player, armor untouched; @return how many were removed. Used
         * only once the migration is complete (flag, no journal): such a mark is an orphan left by a kill between the
         * commit's in-memory unmark and the next player save.
         */
        int dropSwapMarks();
        /** a fresh signed stack for {@code d}; null = template missing */
        S create(EmberItemData d);
        /** the P1 data the worn stack carries (signature-verified), null = none */
        EmberItemData readWorn(int i);
        /** true when a stack with this uid is anywhere on the player (armor + inventory) */
        boolean holds(String uid);
        /** remove the stack with this uid from wherever it is on the player; true when one was removed */
        boolean take(String uid);
        /** write the player file (armor + backpack + marks) now; false = the write did not go through (it may or may not be on disk) */
        boolean persistInventory();
        void remember(EmberItemData d);
        /** DB: this uid is void (state retired, trust cache updated now; the DB write is async and idempotent) */
        void retire(EmberItemData d);
        void alert(String msg);
        String newUid();
        long nowSec();
    }

    private final Port<S> port;
    public String lastDetail = "";

    public EmberSixMigration(Port<S> port) { this.port = port; }

    // ------------------------------------------------------------------ pure parts

    /** §3.2: the 4 pieces for this charm (null / invalid / T0 charm → 4 × T0 with the charm's quality / craft, normally q0 f0). */
    public static EmberItemData[] plan(EmberItemData charm, String[] uids, long nowSec, String run) {
        EmberItemData[] out = new EmberItemData[4];
        boolean real = charm != null && charm.isCharm() && charm.validate() == null && charm.tier >= 1;
        int tier = real ? charm.tier : 0;
        String fam = real ? charm.family : "none";
        int q = charm != null && charm.isCharm() && charm.validate() == null ? charm.quality : 0;
        int f = charm != null && charm.isCharm() && charm.validate() == null ? charm.craft : 0;
        EmberItemData.Origin o = EmberItemData.Origin.of(ORIGIN_MAP, ORIGIN_SRC, run, nowSec);
        for (int i = 0; i < 4; i++) {
            String slot = EmberItemData.ARMOR_SLOTS.get(i);
            out[i] = new EmberItemData(uids[i], EmberItemData.templateId(fam, slot, tier), fam, slot, tier, q, f, 0, 0, true,
                    SOURCE, EmberItemData.DATA_VERSION, 0, 0, 0, 0, 0, o);
        }
        return out;
    }

    /** bit pattern of everything §3.1 pins (H0 / H / D / M / EHP / B) */
    public static long[] bits(EmberLoadout l) {
        return new long[]{Double.doubleToLongBits(l.h0), Double.doubleToLongBits(l.h), Double.doubleToLongBits(l.d),
                Double.doubleToLongBits(l.m), Double.doubleToLongBits(l.ehp()), Double.doubleToLongBits(l.b)};
    }

    /** null when the six-slot loadout with {@code armor} equals the 2-slot one bit for bit, else what differs */
    public static String check(Snapshot s, EmberItemData[] armor) {
        EmberLoadout two = EmberLoadout.compute(s.t, s.blade, s.charm, s.level, s.festHp, s.festDef);
        EmberLoadout six = EmberLoadout.compute(s.t, s.blade, s.charm, s.level, s.festHp, s.festDef, armor);
        long[] a = bits(two), b = bits(six);
        if (Arrays.equals(a, b)) return null;
        return String.format(java.util.Locale.ROOT, "H0 %s→%s H %s→%s D %s→%s", two.h0, six.h0, two.h, six.h, two.d, six.d);
    }

    // ------------------------------------------------------------------ run

    /**
     * D319 (b)+(c): the migration is complete (flag set, no journal) but the player still carries an {@code ember_six_m_*}
     * mark — the commit took it off in memory only and the server was killed before the next player save. Take it off
     * and write the player file once (checked). A failed save is only logged: the next completed-migration visit clears it
     * again; the completed state (flag, 待领, issued pieces) is not touched either way.
     */
    private void tidyOrphanMarks() {
        int n = port.dropSwapMarks();
        if (n <= 0) return;
        boolean ok = port.persistInventory();
        lastDetail = "orphan swap mark cleared (" + n + ")" + (ok ? ", saved" : ", player file save failed (cleared again next time)");
        if (!ok) port.alert("六槽孤儿换装标签已在内存清除，但玩家存档失败（下次回枢纽再清；迁移已完成，不受影响）");
    }

    /** the swap mark for a journal whose first issued uid is {@code uid0} */
    static String markFor(String uid0) {
        String m = uid0 == null ? "" : uid0.replaceAll("[^A-Za-z0-9]", "");
        return m.length() > 16 ? m.substring(0, 16) : m;
    }

    /** the same piece under a new uid (re-issue of a piece that left the player before the commit) */
    static EmberItemData reissue(EmberItemData d, String uid) {
        return new EmberItemData(uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft, d.enhance, d.pity, d.bound, d.source,
                d.version, d.rev, d.affix, d.afPity, d.sigCode, d.rerollN, d.origin);
    }

    public Outcome run() {
        Record<S> rec = port.load();
        for (EmberItemData d : rec.voided) port.retire(d); // idempotent; covers a retire lost to a crash / failed DB write
        if (rec.flag) {
            if (rec.journal == null) tidyOrphanMarks();
            return Outcome.ALREADY;
        }
        if (rec.journal != null) return reconcile(rec);

        Snapshot snap = port.snapshot();
        String[] uids = new String[4];
        for (int i = 0; i < 4; i++) uids[i] = port.newUid();
        long now = port.nowSec();
        EmberItemData[] issue = plan(snap.charm, uids, now, "six:" + now);
        String pre = check(snap, issue);
        if (pre != null) {
            lastDetail = "pre-check " + pre;
            port.alert("六槽迁移自检未通过（未发放）：" + pre);
            return Outcome.CHECK_FAILED;
        }
        List<S> stacks = new ArrayList<S>(4);
        for (EmberItemData d : issue) {
            S s = port.create(d);
            if (s == null) { lastDetail = "missing template " + d.ni; return Outcome.NO_TEMPLATE; }
            stacks.add(s);
        }
        List<S> originals = new ArrayList<S>(4);
        for (int i = 0; i < 4; i++) originals.add(port.armor(i));
        String mark = markFor(uids[0]);

        // PREPARED
        rec.journal = new Journal<S>(originals, Arrays.asList(issue), now, false, mark);
        port.save(rec);

        port.apply(stacks, mark, true); // swap + mark, one step
        if (!port.persistInventory()) {
            port.apply(originals, mark, false); // undo the live swap (+ mark); the journal stays → the next visit resolves it
            return saveFailed("after the swap (live swap undone, journal kept)");
        }

        EmberItemData[] worn = new EmberItemData[4];
        for (int i = 0; i < 4; i++) {
            EmberItemData w = port.readWorn(i);
            worn[i] = w != null && w.uid.equals(issue[i].uid) ? w : null;
        }
        String post = null;
        for (int i = 0; i < 4 && post == null; i++) if (worn[i] == null) post = "slot " + i + " not the issued piece";
        if (post == null) post = check(snap, worn);
        if (post != null) {
            lastDetail = "post-check " + post;
            port.alert("六槽迁移自检未通过（已撤回）：" + post);
            rec.journal = rec.journal.with(originals, Arrays.asList(issue), true);
            port.save(rec);
            if (!revert(rec)) return saveFailed("during the revert (journal kept, reverting)");
            return Outcome.CHECK_FAILED;
        }
        boolean saved = commit(rec, null);
        lastDetail = "issued " + issue[0].ni + "…" + (saved ? "" : POST_SAVE_FAILED);
        return Outcome.MIGRATED;
    }

    private Outcome saveFailed(String where) {
        lastDetail = "player file save failed " + where;
        port.alert("六槽迁移暂停：玩家存档失败，未置标记，下次回到枢纽自动续上（" + where + "）");
        return Outcome.SAVE_FAILED;
    }

    /**
     * The inventory state without the swap mark: the originals are on the player as they keep them, the issued pieces
     * never existed in this state. Take any stray issued stack (defensive), save the player file, then drop the journal
     * and void its uids in one record write; retire them in the DB.
     */
    private boolean dropUnswapped(Record<S> rec) {
        Journal<S> j = rec.journal;
        for (EmberItemData d : j.issued) port.take(d.uid);
        if (!port.persistInventory()) return false;
        Record<S> next = rec.copy();
        next.journal = null;
        next.voided.addAll(j.issued);
        port.save(next);
        rec.journal = null; rec.voided.clear(); rec.voided.addAll(next.voided);
        for (EmberItemData d : j.issued) port.retire(d);
        return true;
    }

    /**
     * Revert (post-check failed, or a crash in the middle of one). Unmarked state → nothing was swapped here: drop.
     * Marked → the originals live only in the journal: each goes back into its slot when that slot is empty or holds the
     * issued piece, otherwise (the player put something there) into 待领 — those are crossed out of the journal in the
     * same record write, before anything on the player changes. Then the issued pieces are taken, the slots and the
     * mark are set in one step, the player file is saved, and only then the journal goes (uids voided). Idempotent.
     * @return false = the player file save failed (journal kept, still reverting)
     */
    private boolean revert(Record<S> rec) {
        Journal<S> j = rec.journal;
        if (!port.marked(j.mark)) return dropUnswapped(rec);
        List<S> orig = new ArrayList<S>(j.originals);
        List<S> target = new ArrayList<S>(4);
        Record<S> next = rec.copy();
        boolean moved = false;
        for (int i = 0; i < 4; i++) {
            S cur = port.armor(i);
            EmberItemData w = port.readWorn(i);
            boolean free = cur == null || w != null && w.uid.equals(j.issued.get(i).uid);
            if (free) { target.add(orig.get(i)); continue; }
            target.add(cur);
            if (orig.get(i) != null) { stash(next, j.at, i, orig.get(i)); orig.set(i, null); moved = true; }
        }
        if (moved) {
            next.journal = j = j.with(orig, j.issued, true);
            port.save(next);
            rec.journal = j; rec.seq = next.seq; rec.stash.clear(); rec.stash.putAll(next.stash);
        }
        for (int i = 0; i < 4; i++) {
            String uid = j.issued.get(i).uid;
            EmberItemData w = port.readWorn(i);
            if (!(w != null && uid.equals(w.uid)) && port.holds(uid)) port.take(uid);
        }
        port.apply(target, j.mark, false); // originals back + mark off, one step (an issued piece in a slot is replaced)
        if (!port.persistInventory()) return false;
        Record<S> done = rec.copy();
        done.journal = null;
        done.voided.addAll(j.issued);
        port.save(done);
        rec.journal = null; rec.voided.clear(); rec.voided.addAll(done.voided);
        for (EmberItemData d : j.issued) port.retire(d);
        return true;
    }

    /**
     * A journal survived a crash / failed save. Decided by the swap mark (see the class doc), never by where the issued
     * pieces are: unmarked → drop; marked → roll forward: issued pieces no longer on the player are re-issued under new
     * uids (old uids voided in the record first, then retired), each new piece goes into its slot when the slot is empty
     * (else to 待领 in the commit; a slot's current content is never overwritten or taken for an original), the player
     * file is saved, then the commit puts the journal's originals into 待领.
     */
    Outcome reconcile(Record<S> rec) {
        Journal<S> j = rec.journal;
        if (j.reverting) {
            if (!revert(rec)) return saveFailed("finishing the revert (journal kept, reverting)");
            lastDetail = "revert finished";
            return Outcome.CHECK_FAILED;
        }
        if (!port.marked(j.mark)) {
            if (!dropUnswapped(rec)) return saveFailed("before dropping the journal (journal kept)");
            lastDetail = "journal dropped (the swap never reached this inventory)";
            return Outcome.JOURNAL_DROPPED;
        }
        List<EmberItemData> issued = new ArrayList<EmberItemData>(j.issued);
        List<EmberItemData> old = new ArrayList<EmberItemData>();
        List<S> fresh = new ArrayList<S>(Arrays.asList(null, null, null, null));
        for (int i = 0; i < 4; i++) {
            if (port.holds(issued.get(i).uid)) continue;
            EmberItemData n = reissue(issued.get(i), port.newUid());
            S s = port.create(n);
            if (s == null) { lastDetail = "missing template " + n.ni; return Outcome.NO_TEMPLATE; }
            old.add(issued.get(i));
            issued.set(i, n);
            fresh.set(i, s);
        }
        if (!old.isEmpty()) {
            Record<S> next = rec.copy();
            next.journal = j = j.with(j.originals, issued, false);
            next.voided.addAll(old);
            port.save(next); // void first: from here no path can make an old uid valid again
            rec.journal = j; rec.voided.clear(); rec.voided.addAll(next.voided);
            for (EmberItemData d : old) port.retire(d);
            for (int i = 0; i < 4; i++) if (fresh.get(i) != null && port.armor(i) == null) { port.setArmor(i, fresh.get(i)); fresh.set(i, null); }
        }
        if (!port.persistInventory()) return saveFailed("rolling forward (journal kept)");
        boolean saved = commit(rec, fresh);
        lastDetail = "rolled forward" + (old.isEmpty() ? "" : ", re-issued " + old.size()) + (saved ? "" : POST_SAVE_FAILED);
        return Outcome.ROLLED_FORWARD;
    }

    private static <S> void stash(Record<S> next, long at, int slot, S o) {
        next.seq++;
        String id = "m" + at + "_" + next.seq;
        next.stash.put(id, new StashEntry<S>(id, slot, o));
    }

    /** lastDetail suffix: the player save right after the commit's unmark failed (the orphan mark is cleared next visit) */
    static final String POST_SAVE_FAILED = " · post-commit player save failed (orphan swap mark cleared next visit)";

    /**
     * DB remember, then flag + done_at + 待领 (originals, plus re-issued pieces that found no free slot) + journal cleared in
     * one record write; then the mark comes off and the player file is written at once (D319 (c), checked). @return false
     * when that last save failed: logged + alerted only, the migration stays complete; the next completed-migration
     * visit clears the orphan mark and saves again ({@link #tidyOrphanMarks}).
     */
    private boolean commit(Record<S> rec, List<S> extra) {
        Journal<S> j = rec.journal;
        for (EmberItemData d : j.issued) port.remember(d);
        Record<S> next = rec.copy();
        for (int i = 0; i < 4; i++) if (j.originals.get(i) != null) stash(next, j.at, i, j.originals.get(i));
        if (extra != null) for (int i = 0; i < 4; i++) if (extra.get(i) != null) stash(next, j.at, i, extra.get(i));
        next.flag = true;
        next.doneAt = port.nowSec();
        next.journal = null;
        port.save(next); // flag + 待领 + journal cleared in one record write
        rec.flag = true; rec.doneAt = next.doneAt; rec.journal = null; rec.seq = next.seq; rec.stash.clear(); rec.stash.putAll(next.stash);
        port.apply(null, j.mark, false); // tidy: the mark is no longer needed (null armor = slots untouched)
        boolean saved = port.persistInventory(); // D319 (c): without it a kill before the auto-save leaves an orphan mark
        if (!saved) port.alert("六槽迁移已完成，但去掉换装标签后的玩家存档失败（下次回枢纽时清掉残留标签并补存；迁移结果不受影响）");
        return saved;
    }

    /** re-send the DB retire of every voided uid (join; the async write of an earlier one may have been lost) */
    public static <S> int resendVoids(Port<S> port) {
        Record<S> rec = port.load();
        for (EmberItemData d : rec.voided) port.retire(d);
        return rec.voided.size();
    }

    // ------------------------------------------------------------------ 待领 (claim, exactly once)

    /** Claim side effects. */
    public interface ClaimPort<S> {
        /**
         * put the stack (tagged with the entry id) into free backpack space and the hand-out mark for the entry on the
         * player, as one step (saved together in the player file); false = no room, nothing changed
         */
        boolean give(S stack, String entryId);
        /** the hand-out mark for this entry is on the player: the stack was handed out in this inventory state (wherever it is now) */
        boolean tagged(String entryId);
        /** remove the claim tag from the stack and the hand-out mark (it is the player's own item from now on) */
        void untag(String entryId);
        /** write the player file now; false = the write did not go through */
        boolean persistInventory();
        /** take the stack tagged with this entry id back off the player and remove its mark, one step (its save failed, the entry is still owed) */
        boolean revoke(String entryId);
        /** strip claim tags / hand-out marks whose entry is no longer owed (crash between record save and untag) */
        void untagExcept(java.util.Set<String> owed);
    }

    /**
     * Claims every entry that fits. Exactly once: give (tagged stack + hand-out mark, one step) → player file saved →
     * record save without the entry → untag. A crash after the give leaves the mark in that inventory state;
     * {@link #settleClaims} then drops the entry instead of giving again — even if the player has since thrown or stored
     * the stack (the mark is on the player, not on the stack).
     * @return {claimed, left}
     */
    public static <S> int[] claim(Port<S> port, ClaimPort<S> cp) {
        Record<S> rec = port.load();
        settleClaims(port, cp, rec);
        int got = 0;
        for (StashEntry<S> e : new ArrayList<StashEntry<S>>(rec.stash.values())) {
            if (!cp.give(e.item, e.id)) break;
            if (!cp.persistInventory()) {
                cp.revoke(e.id); // live = record again (entry owed, stack gone); a copy that did reach the disk is settled by the tag
                throw new SaveFailed("player file save failed after handing out " + e.id + " (" + got + " claimed before)");
            }
            rec.stash.remove(e.id);
            port.save(rec);
            cp.untag(e.id);
            got++;
        }
        cp.persistInventory(); // only untags since the last checked save; a leftover tag is stripped by settleClaims (untagExcept)
        return new int[]{got, rec.stash.size()};
    }

    /**
     * {@link #claim} with a per-player in-flight guard: a second claim for the same key while one is running (repeated
     * click, re-entrant event) returns null and touches nothing.
     */
    public static <S> int[] claimGuarded(java.util.Set<Object> busy, Object key, Port<S> port, ClaimPort<S> cp) {
        if (!busy.add(key)) return null;
        try {
            return claim(port, cp);
        } finally {
            busy.remove(key);
        }
    }

    /** drop entries whose hand-out already reached this inventory state (crash between give and save); untag leftovers */
    public static <S> int settleClaims(Port<S> port, ClaimPort<S> cp, Record<S> rec) {
        int n = 0;
        for (StashEntry<S> e : new ArrayList<StashEntry<S>>(rec.stash.values())) {
            if (cp.tagged(e.id)) { rec.stash.remove(e.id); n++; }
        }
        if (n > 0) {
            // the tagged stacks are live; the entries go only once the player file holds them
            if (!cp.persistInventory()) throw new SaveFailed("player file save failed while settling " + n + " handed-out entries");
            port.save(rec);
        }
        cp.untagExcept(new java.util.HashSet<String>(rec.stash.keySet()));
        return n;
    }
}
