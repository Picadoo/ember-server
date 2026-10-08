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
 */
public final class EmberSixMigration<S> {

    public static final String FLAG = "p1_six_mig";
    public static final String ORIGIN_SRC = "X04";
    public static final String ORIGIN_MAP = "migrate";
    public static final String SOURCE = "migrate";

    public enum Outcome { ALREADY, MIGRATED, ROLLED_FORWARD, JOURNAL_DROPPED, CHECK_FAILED, NO_TEMPLATE, ERROR }

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

    /** Side effects; each call may throw (crash injection in tests). */
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
        void persistInventory();
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
        port.persistInventory();

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
            revert(rec);
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
    private void revert(Record<S> rec) {
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
        port.persistInventory();
        rec.journal = null;
        port.save(rec);
    }

    /**
     * A journal survived a crash: decide from what the player actually holds. Nothing issued on the player → the swap
     * never reached the player file, the originals are where they were → drop the journal. Some issued → finish the
     * swap: slots whose piece is missing get it now (the live content of that slot is first written into the journal as
     * its original, so a second crash cannot lose or double it), then commit.
     */
    Outcome reconcile(Record<S> rec) {
        Journal<S> j = rec.journal;
        if (j.reverting) { revert(rec); lastDetail = "revert finished"; return Outcome.CHECK_FAILED; }
        boolean[] has = new boolean[4];
        boolean any = false;
        for (int i = 0; i < 4; i++) { has[i] = port.holds(j.issued.get(i).uid); any |= has[i]; }
        if (!any) {
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
            port.persistInventory();
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
        void persistInventory();
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
            cp.persistInventory();
            rec.stash.remove(e.id);
            port.save(rec);
            cp.untag(e.id);
            got++;
        }
        cp.persistInventory();
        return new int[]{got, rec.stash.size()};
    }

    /** drop entries whose tagged stack already reached the player (crash between give and save); untag leftovers */
    public static <S> int settleClaims(Port<S> port, ClaimPort<S> cp, Record<S> rec) {
        int n = 0;
        for (StashEntry<S> e : new ArrayList<StashEntry<S>>(rec.stash.values())) {
            if (cp.tagged(e.id)) { rec.stash.remove(e.id); n++; }
        }
        if (n > 0) port.save(rec);
        cp.untagExcept(new java.util.HashSet<String>(rec.stash.keySet()));
        return n;
    }
}
