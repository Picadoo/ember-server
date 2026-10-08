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
 * <p>Order (crash-safe, at most one issue):
 * <ol>
 *   <li>flag {@code p1_six_mig} set → return (idempotent);</li>
 *   <li>a journal left by an earlier crash → {@link #reconcile}: issued uids still worn → roll forward (DB remember,
 *       originals into 待领, flag); none worn → the inventory never saw the swap → drop the journal;</li>
 *   <li>plan 4 pieces (charm family / tier / quality / craft, enhance 0, source migrate, bound, origin X04; no or T0 charm
 *       → 4 × T0) and <b>pre-check</b> H0/H/D/M/EHP bits (2-slot vs six-slot with the planned pieces) — mismatch → nothing
 *       happens, OP alert;</li>
 *   <li>build the stacks (missing NI template → nothing happens);</li>
 *   <li>save the journal (originals + issued data) — PREPARED;</li>
 *   <li>swap the armor slots and save the player file;</li>
 *   <li><b>post-check</b> from the stacks actually worn — mismatch → put the originals back, drop the journal, OP alert
 *       (the issued uids were never written to the DB, so nothing is left behind);</li>
 *   <li>DB remember the issued pieces; commit = flag 1 + originals into 待领 + journal cleared, one atomic record save.</li>
 * </ol>
 *
 * <p><b>Persist before mark</b> (T1-4 acceptance fix): every record write that relies on the player's inventory being in
 * some state (commit / journal dropped / 待领 entry removed) comes <i>after</i> a successful player-file save of exactly
 * that state. {@link Port#persistInventory} reports success; a failed save never sets the flag or drops / clears
 * anything — the live inventory is put back to what the record still describes where that is possible, the journal or
 * 待领 entry stays, the run returns {@link Outcome#SAVE_FAILED} (claim throws {@link SaveFailed}), and the next visit
 * resumes. A hard kill at any point therefore finds a record that never runs ahead of the player file on disk.
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

    /** One 待领 entry: an item that sat in an armor slot before the migration. */
    public static final class StashEntry<S> {
        public final String id;
        public final int slot;
        public final S item;
        public StashEntry(String id, int slot, S item) { this.id = id; this.slot = slot; this.item = item; }
    }

    /** PREPARED journal: what was in the slots and what is being issued (P1 order head, chest, legs, boots). */
    public static final class Journal<S> {
        public final List<S> originals;          // size 4, null = empty slot
        public final List<EmberItemData> issued; // size 4
        public final long at;
        /** true once the post-check failed: a crash mid-revert finishes the revert, never the issue */
        public final boolean reverting;
        public Journal(List<S> originals, List<EmberItemData> issued, long at) { this(originals, issued, at, false); }
        public Journal(List<S> originals, List<EmberItemData> issued, long at, boolean reverting) {
            this.originals = originals; this.issued = issued; this.at = at; this.reverting = reverting;
        }
    }

    /** The per-player record file (flag + journal + 待领). */
    public static final class Record<S> {
        public boolean flag;
        public Journal<S> journal;
        public final LinkedHashMap<String, StashEntry<S>> stash = new LinkedHashMap<String, StashEntry<S>>();
        public int seq;
        public Record<S> copy() {
            Record<S> r = new Record<S>();
            r.flag = flag; r.journal = journal; r.seq = seq; r.stash.putAll(stash);
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
        /** a fresh signed stack for {@code d}; null = template missing */
        S create(EmberItemData d);
        /** the P1 data the worn stack carries (signature-verified), null = none */
        EmberItemData readWorn(int i);
        /** true when a stack with this uid is anywhere on the player (armor + inventory) */
        boolean holds(String uid);
        /** remove the stack with this uid from wherever it is on the player; true when one was removed */
        boolean take(String uid);
        /** write the player file (armor + backpack) now; false = the write did not go through (it may or may not be on disk) */
        boolean persistInventory();
        void remember(EmberItemData d);
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

    public Outcome run() {
        Record<S> rec = port.load();
        if (rec.flag) return Outcome.ALREADY;
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

        // PREPARED
        rec.journal = new Journal<S>(originals, Arrays.asList(issue), now);
        port.save(rec);

        for (int i = 0; i < 4; i++) port.setArmor(i, stacks.get(i));
        if (!port.persistInventory()) {
            // undo the live swap; the journal stays (the disk may hold either state) → the next visit drops it or rolls forward
            for (int i = 0; i < 4; i++) port.setArmor(i, originals.get(i));
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
            rec.journal = new Journal<S>(originals, Arrays.asList(issue), now, true);
            port.save(rec);
            if (!revert(rec)) return saveFailed("during the revert (journal kept, reverting)");
            return Outcome.CHECK_FAILED;
        }
        commit(rec);
        lastDetail = "issued " + issue[0].ni + "…";
        return Outcome.MIGRATED;
    }

    /**
     * Revert (post-check failed, or a crash in the middle of one). Per slot, idempotent: the issued piece still in its
     * slot → the original overwrites it (one call, nothing in between); the issued piece moved elsewhere → the original
     * goes to 待领 and is crossed out of the journal in the same record write, then the piece is taken; the issued piece
     * gone → that slot is already back. Issued uids were never written to the DB, so they simply stop existing. No flag.
     */
    private boolean revert(Record<S> rec) {
        for (int i = 0; i < 4; i++) {
            Journal<S> j = rec.journal;
            String uid = j.issued.get(i).uid;
            EmberItemData w = port.readWorn(i);
            if (w != null && uid.equals(w.uid)) { port.setArmor(i, j.originals.get(i)); continue; }
            if (!port.holds(uid)) continue;
            S o = j.originals.get(i);
            if (o != null) {
                List<S> orig = new ArrayList<S>(j.originals);
                orig.set(i, null);
                stash(rec, j.at, i, o);
                rec.journal = new Journal<S>(orig, j.issued, j.at, true);
                port.save(rec);
            }
            port.take(uid);
        }
        if (!port.persistInventory()) return false; // journal (reverting) stays: the next visit finishes the revert
        rec.journal = null;
        port.save(rec);
        return true;
    }

    private Outcome saveFailed(String where) {
        lastDetail = "player file save failed " + where;
        port.alert("六槽迁移暂停：玩家存档失败，未置标记，下次回到枢纽自动续上（" + where + "）");
        return Outcome.SAVE_FAILED;
    }

    /**
     * A journal survived a crash: decide from what the player actually holds. Nothing issued on the player → the swap
     * never reached the player file, the originals are where they were → drop the journal. Some issued → finish the
     * swap: slots whose piece is missing get it now (the live content of that slot is first written into the journal as
     * its original, so a second crash cannot lose or double it), then commit.
     */
    Outcome reconcile(Record<S> rec) {
        Journal<S> j = rec.journal;
        if (j.reverting) {
            if (!revert(rec)) return saveFailed("finishing the revert (journal kept, reverting)");
            lastDetail = "revert finished";
            return Outcome.CHECK_FAILED;
        }
        boolean[] has = new boolean[4];
        boolean any = false;
        for (int i = 0; i < 4; i++) { has[i] = port.holds(j.issued.get(i).uid); any |= has[i]; }
        if (!any) {
            // the originals are on the player: make the player file say so before the journal (their only other copy) goes
            if (!port.persistInventory()) return saveFailed("before dropping the journal (journal kept)");
            rec.journal = null;
            port.save(rec);
            lastDetail = "journal dropped (nothing issued)";
            return Outcome.JOURNAL_DROPPED;
        }
        boolean all = true;
        for (boolean h : has) all &= h;
        if (!all) {
            List<S> orig = new ArrayList<S>(j.originals);
            List<S> stacks = new ArrayList<S>(4);
            for (int i = 0; i < 4; i++) {
                stacks.add(null);
                if (has[i]) continue;
                S s = port.create(j.issued.get(i));
                if (s == null) { lastDetail = "missing template " + j.issued.get(i).ni; return Outcome.NO_TEMPLATE; }
                stacks.set(i, s);
                orig.set(i, port.armor(i));
            }
            rec.journal = j = new Journal<S>(orig, j.issued, j.at, false);
            port.save(rec);
            for (int i = 0; i < 4; i++) if (!has[i]) port.setArmor(i, stacks.get(i));
            if (!port.persistInventory()) {
                for (int i = 0; i < 4; i++) if (!has[i]) port.setArmor(i, orig.get(i)); // back to what the journal records as live
                return saveFailed("rolling forward (journal kept)");
            }
        } else if (!port.persistInventory()) {
            // all four were already on the player, but the live state may never have reached the player file (a save that
            // failed / crashed before this visit): write it now, and only then set the flag
            return saveFailed("rolling forward, all pieces held (journal kept)");
        }
        commit(rec);
        lastDetail = "rolled forward";
        return Outcome.ROLLED_FORWARD;
    }

    private static <S> void stash(Record<S> next, long at, int slot, S o) {
        next.seq++;
        String id = "m" + at + "_" + next.seq;
        next.stash.put(id, new StashEntry<S>(id, slot, o));
    }

    private void commit(Record<S> rec) {
        Journal<S> j = rec.journal;
        for (EmberItemData d : j.issued) port.remember(d);
        Record<S> next = rec.copy();
        for (int i = 0; i < 4; i++) if (j.originals.get(i) != null) stash(next, j.at, i, j.originals.get(i));
        next.flag = true;
        next.journal = null;
        port.save(next); // flag + 待领 + journal cleared in one record write
        rec.flag = true; rec.journal = null; rec.seq = next.seq; rec.stash.clear(); rec.stash.putAll(next.stash);
    }

    // ------------------------------------------------------------------ 待领 (claim, exactly once)

    /** Claim side effects. */
    public interface ClaimPort<S> {
        /** put the stack (tagged with the entry id) into free backpack space; false = no room */
        boolean give(S stack, String entryId);
        /** true when a stack tagged with this entry id is on the player */
        boolean tagged(String entryId);
        /** remove the claim tag from the stack (it is the player's own item from now on) */
        void untag(String entryId);
        /** write the player file now; false = the write did not go through */
        boolean persistInventory();
        /** take the stack tagged with this entry id back off the player (its save failed, the entry is still owed) */
        boolean revoke(String entryId);
        /** strip claim tags whose entry is no longer owed (crash between record save and untag) */
        void untagExcept(java.util.Set<String> owed);
    }

    /**
     * Claims every entry that fits. Exactly once: give (tagged) → record save without the entry → untag. A crash after the
     * give leaves a tagged stack; {@link #settleClaims} then drops the entry instead of giving again.
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

    /** drop entries whose tagged stack already reached the player (crash between give and save); untag leftovers */
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
