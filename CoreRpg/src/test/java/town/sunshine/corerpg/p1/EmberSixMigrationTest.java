package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * D318 六槽 T1-4（迁移幂等：连跑 / 每个副作用点抛异常 / 发放与置标记之间关服）、T1-5（有护符 / 无护符 / 原版甲位 /
 * 背包满 前后 H·D 逐位；自检失败自动撤回）、T1-6（离线部分：甲位 ↔ 背包 ↔ 待领 的资产守恒、同 uid 不占两格）.
 * Stacks are strings: "P1:&lt;uid&gt;" = a P1 piece, anything else = a vanilla / foreign item; "…#&lt;id&gt;" = claim-tagged.
 */
public class EmberSixMigrationTest {

    static final class Crash extends RuntimeException { Crash() { super("injected crash"); } }

    static final EmberTables T = EmberTables.defaults();

    static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    /** the whole fake player + server: live inventory, player file on disk, record file, DB */
    static class World implements EmberSixMigration.Port<String>, EmberSixMigration.ClaimPort<String> {
        String[] armor = new String[4];
        String[] pack = new String[36];
        String[] diskArmor, diskPack;
        EmberSixMigration.Record<String> disk = new EmberSixMigration.Record<String>();
        final Set<String> db = new HashSet<String>();
        final Map<String, EmberItemData> created = new HashMap<String, EmberItemData>();
        final Set<String> everIssued = new HashSet<String>();
        EmberItemData blade, charm;
        int level = 30;
        int calls, crashAt = -1;
        boolean tamper, noTemplate;
        int uidSeq;

        World() { persistInventory0(); }

        void tick() { calls++; if (calls == crashAt) throw new Crash(); }
        void persistInventory0() { diskArmor = armor.clone(); diskPack = pack.clone(); }
        /** hard kill: the live inventory is lost, the player file on disk comes back */
        void hardRestart() { armor = diskArmor.clone(); pack = diskPack.clone(); crashAt = -1; clearFail(); }
        /** graceful stop: the server saves the player on the way down */
        void softRestart() { persistInventory0(); crashAt = -1; clearFail(); }

        /** player-file save failure injection: 0 none, 1 every save fails, n ≥ 2 only the n-th save from now fails */
        int failMode;
        int persistsSinceArm;
        /** a failed save still reached the disk (the outcome is unknown to the caller) */
        boolean ghost;
        int failedSaves;
        void failSaves(int mode, boolean ghostWrite) { failMode = mode; ghost = ghostWrite; persistsSinceArm = 0; }
        void clearFail() { failMode = 0; ghost = false; persistsSinceArm = 0; }

        static EmberSixMigration.Record<String> copy(EmberSixMigration.Record<String> r) {
            EmberSixMigration.Record<String> c = r.copy();
            if (r.journal != null) c.journal = new EmberSixMigration.Journal<String>(new ArrayList<String>(r.journal.originals),
                    new ArrayList<EmberItemData>(r.journal.issued), r.journal.at, r.journal.reverting);
            return c;
        }

        public EmberSixMigration.Record<String> load() { return copy(disk); }
        public void save(EmberSixMigration.Record<String> r) { tick(); disk = copy(r); }
        public EmberSixMigration.Snapshot snapshot() { return new EmberSixMigration.Snapshot(T, blade, charm, level, 0, 0); }
        public String armor(int i) { return armor[i]; }
        public void setArmor(int i, String s) { tick(); armor[i] = s; }
        public String create(EmberItemData d) {
            tick();
            if (noTemplate) return null;
            created.put(d.uid, d);
            everIssued.add(d.uid);
            return "P1:" + d.uid;
        }
        public EmberItemData readWorn(int i) {
            String s = armor[i];
            if (s == null || !s.startsWith("P1:")) return null;
            EmberItemData d = created.get(s.substring(3));
            if (d != null && tamper) d = new EmberItemData(d.uid, d.ni, d.family, d.slot, d.tier, d.quality, d.craft < EmberTables.MAX_CRAFT ? d.craft + 1 : d.craft - 1, 0, 0, true,
                    d.source, d.version, d.rev, 0, 0, 0, 0, d.origin);
            return d;
        }
        List<String> all() {
            List<String> l = new ArrayList<String>();
            for (String s : armor) if (s != null) l.add(s);
            for (String s : pack) if (s != null) l.add(s);
            return l;
        }
        public boolean holds(String uid) { for (String s : all()) if (s.equals("P1:" + uid)) return true; return false; }
        public boolean take(String uid) {
            tick();
            for (int i = 0; i < 4; i++) if (("P1:" + uid).equals(armor[i])) { armor[i] = null; return true; }
            for (int i = 0; i < 36; i++) if (("P1:" + uid).equals(pack[i])) { pack[i] = null; return true; }
            return false;
        }
        public boolean persistInventory() {
            tick();
            persistsSinceArm++;
            if (failMode == 1 || failMode >= 2 && persistsSinceArm == failMode) {
                failedSaves++;
                if (ghost) persistInventory0();
                return false;
            }
            persistInventory0();
            return true;
        }
        public void remember(EmberItemData d) { tick(); db.add(d.uid); }
        public void alert(String msg) { }
        public String newUid() { uidSeq++; return EmberItemData.newUid(); }
        public long nowSec() { return 1760000000L; }

        // claim
        public boolean give(String stack, String id) {
            tick();
            for (int i = 0; i < 36; i++) if (pack[i] == null) { pack[i] = stack + "#" + id; return true; }
            return false;
        }
        public boolean tagged(String id) { for (String s : pack) if (s != null && s.endsWith("#" + id)) return true; return false; }
        public void untag(String id) {
            tick();
            for (int i = 0; i < 36; i++) if (pack[i] != null && pack[i].endsWith("#" + id)) pack[i] = pack[i].substring(0, pack[i].length() - id.length() - 1);
        }
        public boolean revoke(String id) {
            tick();
            for (int i = 0; i < 36; i++) if (pack[i] != null && pack[i].endsWith("#" + id)) { pack[i] = null; return true; }
            return false;
        }
        public void untagExcept(Set<String> owed) {
            for (int i = 0; i < 36; i++) {
                String s = pack[i];
                if (s == null || s.indexOf('#') < 0) continue;
                String id = s.substring(s.indexOf('#') + 1);
                if (!owed.contains(id)) pack[i] = s.substring(0, s.indexOf('#'));
            }
        }

        String detail = "";
        List<String> f0;
        EmberSixMigration.Outcome migrate() {
            EmberSixMigration<String> m = new EmberSixMigration<String>(this);
            try { return m.run(); } finally { detail = m.lastDetail; }
        }
        EmberItemData[] worn() { EmberItemData[] w = new EmberItemData[4]; boolean t = tamper; tamper = false; for (int i = 0; i < 4; i++) w[i] = readWorn(i); tamper = t; return w; }

        /** every non-P1 item, wherever it is (live inventory + 待领), tags stripped */
        List<String> foreign() {
            List<String> l = new ArrayList<String>();
            for (String s : all()) if (!s.startsWith("P1:")) l.add(strip(s));
            for (EmberSixMigration.StashEntry<String> e : disk.stash.values()) if (!tagged(e.id)) l.add(strip(e.item)); // tagged = already handed out (settled on the next claim)
            if (disk.journal != null) // mid-migration: an original whose slot already holds the issued piece lives in the journal
                for (int i = 0; i < 4; i++) if (disk.journal.originals.get(i) != null && holds(disk.journal.issued.get(i).uid)) l.add(disk.journal.originals.get(i));
            Collections.sort(l);
            return l;
        }
        List<String> p1OnPlayer() { List<String> l = new ArrayList<String>(); for (String s : all()) if (s.startsWith("P1:")) l.add(strip(s)); return l; }
        static String strip(String s) { int i = s.indexOf('#'); return i < 0 ? s : s.substring(0, i); }
    }

    static World world(int kase) {
        World w = new World();
        switch (kase) {
            case 0: // has charm, vanilla items in the armor slots
                w.charm = piece("burst", "charm", 2, 1, 2, 4);
                w.blade = piece("scorch", "blade", 2, 2, 1, 3);
                w.armor = new String[]{"iron_helmet", null, "diamond_leggings", "other_players_p1_boots"};
                break;
            case 1: // no charm
                w.blade = piece("scorch", "blade", 1, 0, 0, 0);
                w.armor = new String[]{null, "leather_chest", null, null};
                break;
            case 2: // T3 charm q3 f5, empty armor
                w.charm = piece("sustain", "charm", 3, 3, 3, 10);
                break;
            case 3: // backpack full + all four slots used
                w.charm = piece("scorch", "charm", 1, 2, 3, 7);
                w.armor = new String[]{"a0", "a1", "a2", "a3"};
                for (int i = 0; i < 36; i++) w.pack[i] = "fill" + i;
                break;
            default: // T0 charm
                w.charm = piece("none", "charm", 0, 0, 0, 0);
                w.armor = new String[]{"chain_helmet", null, null, null};
        }
        w.persistInventory0();
        return w;
    }

    static List<String> initialForeign(World w) { return w.foreign(); }

    static void assertMigrated(World w, List<String> foreign0, String why) {
        assertTrue(why + " flag", w.disk.flag);
        assertNull(why + " journal", w.disk.journal);
        assertEquals(why + " nothing lost / doubled", foreign0, w.foreign());
        List<String> p1 = w.p1OnPlayer();
        assertEquals(why + " exactly four issued pieces", 4, p1.size());
        assertEquals(why + " distinct uids", 4, new HashSet<String>(p1).size());
        Set<String> uids = new HashSet<String>();
        for (String s : p1) uids.add(s.substring(3));
        assertEquals(why + " DB = worn", uids, w.db);
        EmberItemData[] worn = w.worn();
        for (int i = 0; i < 4; i++) {
            assertNotNull(why + " slot " + i, worn[i]);
            assertEquals(EmberItemData.ARMOR_SLOTS.get(i), worn[i].slot);
            assertEquals("migrate", worn[i].source);
            assertEquals("X04", worn[i].origin.src);
            assertNull(worn[i].validate());
        }
        assertNull(why + " H/D bits", EmberSixMigration.check(w.snapshot(), worn));
    }

    // ------------------------------------------------------------------ T1-4

    @Test public void twiceIsOnce() {
        for (int k = 0; k < 5; k++) {
            World w = world(k);
            List<String> f0 = w.foreign();
            assertEquals("case " + k + " " + w.detail, EmberSixMigration.Outcome.MIGRATED, w.migrate());
            assertMigrated(w, f0, "case " + k);
            Set<String> db = new HashSet<String>(w.db);
            List<String> inv = w.all();
            assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
            assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
            assertEquals(db, w.db);
            assertEquals(inv, w.all());
            assertEquals(4, w.everIssued.size());
        }
    }

    /** crash at every side-effect call, three restart kinds (none / graceful / hard kill), then rerun → exactly one issue */
    @Test public void crashAtEveryStepIssuesOnceOrNever() {
        int scenarios = 0;
        for (int k = 0; k < 5; k++) {
            World probe = world(k);
            probe.migrate();
            int steps = probe.calls;
            assertTrue(steps >= 10);
            for (int crash = 1; crash <= steps; crash++) {
                for (int restart = 0; restart < 3; restart++) {
                    World w = world(k);
                    List<String> f0 = w.foreign();
                    w.crashAt = crash;
                    try { w.migrate(); fail("crash " + crash + " not hit"); } catch (Crash expected) { }
                    if (restart == 1) w.softRestart(); else if (restart == 2) w.hardRestart(); else w.crashAt = -1;
                    // after the crash: never half-issued in the DB, never a lost / doubled foreign item
                    assertTrue(w.db.isEmpty() || w.db.size() == 4 || !w.disk.flag);
                    EmberSixMigration.Outcome o = w.migrate();
                    String why = "case " + k + " crash@" + crash + " restart " + restart + " → " + o;
                    if (o == EmberSixMigration.Outcome.JOURNAL_DROPPED) o = w.migrate(); // nothing had been issued: the next visit migrates
                    assertTrue(why, o == EmberSixMigration.Outcome.MIGRATED || o == EmberSixMigration.Outcome.ROLLED_FORWARD
                            || o == EmberSixMigration.Outcome.ALREADY);
                    assertMigrated(w, f0, why);
                    assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
                    // only one set is live: everything created but not worn was never written to the DB
                    for (String u : w.everIssued) if (!w.db.contains(u)) assertFalse(why + " stray " + u, w.holds(u));
                    scenarios++;
                }
            }
        }
        assertTrue(scenarios > 150);
    }

    // ------------------------------------------------------------------ T1-5

    @Test public void invariantPerCaseBitExact() {
        for (int k = 0; k < 5; k++) {
            World w = world(k);
            EmberLoadout before = EmberLoadout.compute(T, w.blade, w.charm, w.level, 0, 0);
            assertEquals(EmberSixMigration.Outcome.MIGRATED, w.migrate());
            EmberLoadout after = EmberLoadout.compute(T, w.blade, w.charm, w.level, 0, 0, w.worn());
            assertArrayEquals("case " + k, EmberSixMigration.bits(before), EmberSixMigration.bits(after));
            EmberItemData[] worn = w.worn();
            if (w.charm != null && w.charm.tier >= 1) {
                for (EmberItemData d : worn) {
                    assertEquals(w.charm.family, d.family);
                    assertEquals(w.charm.tier, d.tier);
                    assertEquals(w.charm.quality, d.quality);
                    assertEquals(w.charm.craft, d.craft);
                    assertEquals(0, d.enhance);
                }
            } else {
                for (EmberItemData d : worn) { assertEquals(0, d.tier); assertEquals(0, d.quality); assertEquals(0, d.craft); }
            }
            assertNotNull("migrated pieces are not dismantlable", EmberUpgradeRules.dismantleCheck(worn[0]));
        }
        // the pre-check is a real gate: a piece with a different quality / craft is caught bit-wise
        World w = world(2);
        EmberItemData[] off = EmberSixMigration.plan(w.charm, new String[]{"a", "b", "c", "d"}, 1L, "t");
        off[2] = piece("sustain", "legs", 3, 2, 5, 0);
        assertNotNull(EmberSixMigration.check(w.snapshot(), off));
    }

    @Test public void failedSelfCheckRevertsEverything() {
        for (int k = 0; k < 5; k++) {
            if (k == 1) continue; // no charm: armor adds nothing, so a wrong piece cannot change H (and does not matter)
            World w = world(k);
            List<String> f0 = w.foreign();
            String[] armor0 = w.armor.clone();
            w.tamper = true;
            assertEquals(EmberSixMigration.Outcome.CHECK_FAILED, w.migrate());
            assertFalse(w.disk.flag);
            assertNull(w.disk.journal);
            assertTrue("no DB row", w.db.isEmpty());
            assertTrue("no P1 piece left", w.p1OnPlayer().isEmpty());
            assertArrayEquals("originals back in their slots", armor0, w.armor);
            assertTrue(w.disk.stash.isEmpty());
            assertEquals(f0, w.foreign());
            w.tamper = false;
            assertEquals(EmberSixMigration.Outcome.MIGRATED, w.migrate());
            assertMigrated(w, f0, "after revert");
        }
    }

    @Test public void crashDuringRevertFinishesTheRevert() {
        World probe = world(0);
        probe.tamper = true;
        probe.migrate();
        int steps = probe.calls;
        for (int crash = 1; crash <= steps; crash++) for (int restart = 0; restart < 3; restart++) {
            World w = world(0);
            List<String> f0 = w.foreign();
            w.tamper = true;
            w.crashAt = crash;
            try { w.migrate(); } catch (Crash expected) { }
            if (restart == 1) w.softRestart(); else if (restart == 2) w.hardRestart(); else w.crashAt = -1;
            w.tamper = false;
            EmberSixMigration.Outcome o = w.migrate();
            if (o != EmberSixMigration.Outcome.MIGRATED && o != EmberSixMigration.Outcome.ROLLED_FORWARD) o = w.migrate();
            String why = "revert crash@" + crash + " restart " + restart + " → " + o;
            assertTrue(why, o == EmberSixMigration.Outcome.MIGRATED || o == EmberSixMigration.Outcome.ROLLED_FORWARD || o == EmberSixMigration.Outcome.ALREADY);
            assertMigrated(w, f0, why);
        }
    }

    @Test public void missingTemplateDoesNothing() {
        World w = world(0);
        String[] a0 = w.armor.clone();
        w.noTemplate = true;
        assertEquals(EmberSixMigration.Outcome.NO_TEMPLATE, w.migrate());
        assertArrayEquals(a0, w.armor);
        assertFalse(w.disk.flag);
        assertNull(w.disk.journal);
        assertTrue(w.db.isEmpty());
    }

    // ------------------------------------------------------------------ 待领 claim (exactly once) + T1-6 offline

    @Test public void claimIsExactlyOnceUnderCrashes() {
        World probe = world(3);
        probe.migrate();
        for (int i = 0; i < 36; i++) probe.pack[i] = null; // make room
        int c0 = probe.calls;
        int[] r = EmberSixMigration.claim(probe, probe);
        assertEquals(4, r[0]);
        assertEquals(0, r[1]);
        int steps = probe.calls - c0;
        for (int crash = 1; crash <= steps; crash++) for (int restart = 0; restart < 3; restart++) {
            World w = world(3);
            w.migrate();
            for (int i = 0; i < 36; i++) w.pack[i] = null;
            w.persistInventory0();
            List<String> f0 = w.foreign();
            w.crashAt = w.calls + crash;
            try { EmberSixMigration.claim(w, w); } catch (Crash expected) { }
            if (restart == 1) w.softRestart(); else if (restart == 2) w.hardRestart(); else w.crashAt = -1;
            EmberSixMigration.claim(w, w);
            String why = "claim crash@" + crash + " restart " + restart;
            assertTrue(why + " stash empty", w.disk.stash.isEmpty());
            assertEquals(why, f0, w.foreign());
            for (String s : w.pack) assertTrue(why + " untagged", s == null || s.indexOf('#') < 0);
        }
    }

    @Test public void fullBackpackKeepsItemsWaiting() {
        World w = world(3);
        w.migrate();
        List<String> f0 = w.foreign();
        int[] r = EmberSixMigration.claim(w, w);
        assertEquals(0, r[0]);
        assertEquals(4, r[1]);
        w.pack[5] = null; w.pack[9] = null;
        f0 = w.foreign();
        r = EmberSixMigration.claim(w, w);
        assertEquals(2, r[0]);
        assertEquals(2, r[1]);
        assertEquals(f0, w.foreign());
    }

    /** random fuzz over migrate / claim / crash / restart: 0 lost, 0 doubled, one uid in one place */
    @Test public void conservationFuzz() {
        Random rnd = new Random(318);
        for (int run = 0; run < 1500; run++) {
            World w = world(rnd.nextInt(5));
            for (int i = 0; i < 36; i++) if (rnd.nextInt(3) == 0) w.pack[i] = null;
            w.persistInventory0();
            List<String> f0 = w.foreign();
            StringBuilder log = new StringBuilder();
            for (int op = 0; op < 8; op++) {
                w.crashAt = rnd.nextInt(3) == 0 ? w.calls + 1 + rnd.nextInt(14) : -1;
                int base = w.calls;
                boolean mig = rnd.nextBoolean();
                log.append(mig ? " mig" : " claim").append(w.crashAt < 0 ? "" : "@" + (w.crashAt - base));
                try {
                    if (mig) log.append("=").append(w.migrate()); else log.append("=").append(Arrays.toString(EmberSixMigration.claim(w, w)));
                } catch (Crash e) {
                    int k = rnd.nextInt(3);
                    log.append(" crash r").append(k);
                    if (k == 1) w.softRestart(); else if (k == 2) w.hardRestart(); else w.crashAt = -1;
                }
                w.crashAt = -1;
                if (rnd.nextInt(4) == 0) { int a = rnd.nextInt(36); if (w.pack[a] != null && !w.pack[a].startsWith("P1:")) w.pack[a] = null; f0 = null; }
                if (f0 != null) assertEquals("run " + run + " op " + op + log, f0, w.foreign());
                List<String> p1 = w.p1OnPlayer();
                assertEquals("uid in one place", p1.size(), new HashSet<String>(p1).size());
                assertTrue(p1.size() <= 4);
            }
            w.crashAt = -1;
            EmberSixMigration.Outcome o = w.migrate();
            if (o == EmberSixMigration.Outcome.JOURNAL_DROPPED) w.migrate();
            assertTrue(w.disk.flag);
            assertEquals(4, w.db.size());
            if (f0 != null) assertEquals(f0, w.foreign());
        }
    }

    static int countOrig(World w) { int n = 0; for (String x : w.all()) { String t = World.strip(x); if (t.matches("a[0-3]")) n++; } return n; }

    /** spec §5.4-1: claim what fits, the rest stays in 待领; a crash / graceful stop / hard kill mid-claim at any step loses or doubles nothing */
    @Test public void partialClaimUnderCrashesAtEveryStep() {
        int scenarios = 0;
        for (int free = 0; free <= 5; free++) {
            World probe = world(3);
            probe.migrate();
            for (int i = 0; i < free; i++) probe.pack[i * 7] = null;
            probe.persistInventory0();
            int c0 = probe.calls;
            int[] r = EmberSixMigration.claim(probe, probe);
            int fit = Math.min(free, 4);
            assertArrayEquals("free " + free, new int[]{fit, 4 - fit}, r);
            int steps = Math.max(1, probe.calls - c0);
            for (int crash = 1; crash <= steps; crash++) for (int restart = 0; restart < 3; restart++) {
                World w = world(3);
                w.migrate();
                for (int i = 0; i < free; i++) w.pack[i * 7] = null;
                w.persistInventory0();
                List<String> f0 = w.foreign();
                w.crashAt = w.calls + crash;
                try { EmberSixMigration.claim(w, w); } catch (Crash expected) { }
                if (restart == 1) w.softRestart(); else if (restart == 2) w.hardRestart(); else w.crashAt = -1;
                String why = "free " + free + " crash@" + crash + " restart " + restart;
                assertEquals(why + " after crash", f0, w.foreign());
                for (int rep = 0; rep < 3; rep++) EmberSixMigration.claim(w, w); // the player clicks again (and again)
                assertEquals(why, f0, w.foreign());
                assertEquals(why + " what fits is in the backpack", fit, countOrig(w));
                assertEquals(why + " the rest still waits", 4 - fit, w.disk.stash.size());
                for (String x : w.pack) assertTrue(why + " untagged", x == null || x.indexOf('#') < 0);
                scenarios++;
            }
        }
        assertTrue(scenarios > 60);
    }

    /** rapid / repeated clicks: each click claims what fits now; nothing is handed out twice */
    @Test public void repeatedClicksNeverDuplicate() {
        World w = world(3);
        w.migrate();
        List<String> f0 = w.foreign();
        for (int i = 0; i < 5; i++) assertArrayEquals(new int[]{0, 4}, EmberSixMigration.claim(w, w)); // full: 5 clicks, nothing moves
        assertEquals(f0, w.foreign());
        w.pack[3] = null;
        f0 = w.foreign();
        assertArrayEquals(new int[]{1, 3}, EmberSixMigration.claim(w, w));
        for (int i = 0; i < 5; i++) assertArrayEquals(new int[]{0, 3}, EmberSixMigration.claim(w, w));
        assertEquals(f0, w.foreign());
        for (int i = 0; i < 36; i++) if (w.pack[i] != null && w.pack[i].startsWith("fill")) w.pack[i] = null;
        f0 = w.foreign();
        assertArrayEquals(new int[]{3, 0}, EmberSixMigration.claim(w, w));
        for (int i = 0; i < 5; i++) assertArrayEquals(new int[]{0, 0}, EmberSixMigration.claim(w, w));
        assertEquals(f0, w.foreign());
        assertEquals(4, countOrig(w));
        assertTrue(w.disk.stash.isEmpty());
    }

    /** a second click arriving while a claim is still running (re-entrant / concurrent) is refused by the per-player guard */
    @Test public void concurrentClickIsRefusedWhileAClaimRuns() {
        final Set<Object> busy = Collections.synchronizedSet(new HashSet<Object>());
        final List<int[]> nested = new ArrayList<int[]>();
        final Object player = "uuid-1";
        World w = new World() {
            @Override public boolean give(String stack, String id) {
                nested.add(EmberSixMigration.claimGuarded(busy, player, this, this)); // the double click lands mid-claim
                return super.give(stack, id);
            }
        };
        w.charm = piece("scorch", "charm", 1, 2, 3, 7);
        w.armor = new String[]{"a0", "a1", "a2", "a3"};
        for (int i = 0; i < 36; i++) w.pack[i] = i < 34 ? "fill" + i : null; // two free slots
        w.persistInventory0();
        w.migrate();
        List<String> f0 = w.foreign();
        int[] r = EmberSixMigration.claimGuarded(busy, player, w, w);
        assertArrayEquals(new int[]{2, 2}, r);
        assertFalse(nested.isEmpty());
        for (int[] n : nested) assertNull("refused, touched nothing", n);
        assertTrue("guard released", busy.isEmpty());
        assertEquals(f0, w.foreign());
        assertEquals(2, countOrig(w));
        // another player is not blocked by this one
        busy.add("uuid-2");
        assertNotNull(EmberSixMigration.claimGuarded(busy, player, new World(), new World()));
        // a crash inside the claim releases the guard (next click works)
        World c = world(3);
        c.migrate();
        c.pack[0] = null;
        c.crashAt = c.calls + 1;
        try { EmberSixMigration.claimGuarded(busy, player, c, c); fail(); } catch (Crash expected) { }
        assertFalse(busy.contains(player));
        c.crashAt = -1;
        assertNotNull(EmberSixMigration.claimGuarded(busy, player, c, c));
    }

    // ------------------------------------------------------------------ T1-4 acceptance fix: persist before mark

    static boolean done(EmberSixMigration.Outcome o) {
        return o == EmberSixMigration.Outcome.MIGRATED || o == EmberSixMigration.Outcome.ROLLED_FORWARD || o == EmberSixMigration.Outcome.ALREADY;
    }

    /** later hub visits with nothing injected until the migration is through (journal dropped / revert finished → visit again) */
    static EmberSixMigration.Outcome settle(World w, String why) {
        EmberSixMigration.Outcome o = null;
        for (int i = 0; i < 4; i++) { o = w.migrate(); if (done(o)) return o; }
        fail(why + " did not settle: " + o + " " + w.detail);
        return o;
    }

    /** soundness at any moment: nothing lost / doubled, one uid in one place, a set flag means the 4 DB pieces are held */
    static void assertSound(World w, List<String> f0, String why) {
        assertEquals(why + " foreign", f0, w.foreign());
        List<String> p1 = w.p1OnPlayer();
        assertEquals(why + " uid in one place", p1.size(), new HashSet<String>(p1).size());
        assertTrue(why + " ≤ 4 P1", p1.size() <= 4);
        if (w.disk.flag) {
            assertNull(why + " journal", w.disk.journal);
            Set<String> held = new HashSet<String>();
            for (String x : p1) held.add(x.substring(3));
            assertEquals(why + " flag set ⇒ the 4 remembered pieces are on the player", w.db, held);
            assertEquals(4, w.db.size());
        } else {
            for (String x : p1) assertTrue(why + " unflagged P1 belongs to the journal",
                    w.disk.journal != null && journalUids(w).contains(x.substring(3)));
        }
    }

    static Set<String> journalUids(World w) {
        Set<String> u = new HashSet<String>();
        for (EmberItemData d : w.disk.journal.issued) u.add(d.uid);
        return u;
    }

    static void restart(World w, int r) { if (r == 1) w.softRestart(); else if (r == 2) w.hardRestart(); else { w.crashAt = -1; w.clearFail(); } }
    static final String[] RS = {"none", "graceful", "hard"};

    /** the 余烬-测试 counterexample, verbatim: crash@10 (player-file save), no restart, resume, hard kill */
    @Test public void qaReproCrashAtSaveResumeThenHardKill() {
        for (int k = 0; k < 5; k++) {
            World w = world(k);
            List<String> f0 = w.foreign();
            w.crashAt = 10;
            try { w.migrate(); fail("crash@10 not hit"); } catch (Crash expected) { }
            w.crashAt = -1;
            assertEquals(EmberSixMigration.Outcome.ROLLED_FORWARD, w.migrate());
            w.hardRestart();
            assertMigrated(w, f0, "case " + k + " resume → hard kill");
            assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
        }
    }

    /** the 余烬-测试 225-scenario pattern: crash at every step × 3 restarts → resume → hard kill; 0 bad */
    @Test public void qa225CrashEveryStepResumeThenHardKill() {
        int pat = 0, bad = 0;
        for (int k = 0; k < 5; k++) {
            World probe = world(k); probe.migrate(); int steps = probe.calls;
            for (int c = 1; c <= steps; c++) for (int r = 0; r < 3; r++) {
                World w = world(k); List<String> f0 = w.foreign();
                w.crashAt = c; try { w.migrate(); } catch (Crash e) { }
                restart(w, r);
                EmberSixMigration.Outcome o = w.migrate(); if (o == EmberSixMigration.Outcome.JOURNAL_DROPPED) o = w.migrate();
                w.hardRestart();
                pat++;
                if (!f0.equals(w.foreign()) || w.p1OnPlayer().size() != 4 || !w.disk.flag) bad++;
            }
        }
        System.out.println("[T1-4] qa225: " + pat + " scenarios, bad " + bad);
        assertEquals(225, pat);
        assertEquals(0, bad);
    }

    /**
     * every write point × 3 restarts, then the resume itself crashed at every write point × 3 restarts, then settle, then a
     * hard kill (twice): always exactly one migrated set, nothing lost / doubled.
     */
    @Test public void resumeThenHardKillAtEveryWritePoint() {
        int scenarios = 0;
        for (int k = 0; k < 5; k++) {
            World probe = world(k); probe.migrate(); int steps = probe.calls;
            for (int c1 = 1; c1 <= steps; c1++) for (int r1 = 0; r1 < 3; r1++) {
                World p2 = prep(k, c1, r1);
                int base = p2.calls;
                p2.migrate();
                int steps2 = p2.calls - base;
                for (int c2 = 0; c2 <= steps2; c2++) for (int r2 = 0; r2 < (c2 == 0 ? 1 : 3); r2++) {
                    World w = prep(k, c1, r1);
                    List<String> f0 = w.f0;
                    String why = "case " + k + " crash@" + c1 + "/" + RS[r1] + " resume crash@" + c2 + "/" + RS[r2];
                    assertSound(w, f0, why + " after 1st crash");
                    if (c2 > 0) {
                        w.crashAt = w.calls + c2;
                        try { w.migrate(); } catch (Crash e) { }
                        restart(w, r2);
                        assertSound(w, f0, why + " after 2nd crash");
                    } else {
                        w.migrate();
                    }
                    w.hardRestart();
                    assertSound(w, f0, why + " hard kill after the resume");
                    settle(w, why);
                    w.hardRestart();
                    assertMigrated(w, f0, why);
                    assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
                    w.hardRestart();
                    assertMigrated(w, f0, why + " 2nd hard kill");
                    scenarios++;
                }
            }
        }
        System.out.println("[T1-4] resume × hard kill at every write point: " + scenarios + " scenarios, bad 0");
        assertTrue(scenarios > 5000);
    }

    static World prep(int k, int c1, int r1) {
        World w = world(k);
        w.f0 = w.foreign();
        w.crashAt = c1;
        try { w.migrate(); fail("crash " + c1 + " not hit"); } catch (Crash e) { }
        restart(w, r1);
        return w;
    }

    /**
     * player-file save failure (reported, optionally the write still landed = ghost) at the 1st / 2nd / every save of up
     * to 3 visits, a restart of each kind between visits, with or without a failing post-check (revert path): a failed save
     * never sets the flag / drops the journal; afterwards settle + hard kill → exactly one set.
     */
    @Test public void saveFailureNeverMarksAndResumes() {
        int scenarios = 0, failed = 0;
        int[] modes = {0, 1, 2};
        for (int k = 0; k < 5; k++) for (int tamper = 0; tamper < 2; tamper++) {
            if (tamper == 1 && k == 1) continue; // no charm: a wrong piece cannot change H
            for (int ghost = 0; ghost < 2; ghost++)
                for (int a = 0; a < 3; a++) for (int ra = 0; ra < 3; ra++)
                    for (int b = 0; b < 3; b++) for (int rb = 0; rb < 3; rb++)
                        for (int c = 0; c < 3; c++) for (int rc = 0; rc < 3; rc++) {
                            World w = world(k);
                            List<String> f0 = w.foreign();
                            int[] mode = {modes[a], modes[b], modes[c]};
                            int[] rs = {ra, rb, rc};
                            String why = "case " + k + " tamper " + tamper + " ghost " + ghost + " modes " + Arrays.toString(mode) + " restarts " + Arrays.toString(rs);
                            for (int v = 0; v < 3; v++) {
                                w.tamper = tamper == 1 && v == 0;
                                boolean flag0 = w.disk.flag;
                                int fs = w.failedSaves;
                                w.failSaves(mode[v], ghost == 1);
                                EmberSixMigration.Outcome o = w.migrate();
                                w.tamper = false;
                                if (w.failedSaves > fs) {
                                    failed++;
                                    assertEquals(why + " visit " + v, EmberSixMigration.Outcome.SAVE_FAILED, o);
                                    assertEquals(why + " a failed save never sets the flag", flag0, w.disk.flag);
                                    if (!flag0) assertTrue(why + " no DB rows before the flag", w.db.isEmpty());
                                } else {
                                    assertNotEquals(why, EmberSixMigration.Outcome.SAVE_FAILED, o);
                                }
                                assertSound(w, f0, why + " visit " + v + " → " + o);
                                restart(w, rs[v]);
                                assertSound(w, f0, why + " visit " + v + " restart " + RS[rs[v]]);
                            }
                            settle(w, why);
                            w.hardRestart();
                            assertMigrated(w, f0, why);
                            assertEquals(EmberSixMigration.Outcome.ALREADY, w.migrate());
                            scenarios++;
                        }
        }
        System.out.println("[T1-4] save failure: " + scenarios + " scenarios (" + failed + " failed saves), bad 0");
        assertTrue(failed > 1000);
    }

    /** 待领 claim: a failed save at every save point (ghost or not) × 3 restarts, then repeated clicks → exactly once */
    @Test public void claimSaveFailureAtEverySavePoint() {
        int scenarios = 0;
        for (int free = 1; free <= 5; free++) for (int n = 1; n <= 6; n++) for (int ghost = 0; ghost < 2; ghost++) for (int r = 0; r < 3; r++) {
            World w = world(3);
            w.migrate();
            for (int i = 0; i < free; i++) w.pack[i * 7] = null;
            w.persistInventory0();
            List<String> f0 = w.foreign();
            int stash0 = w.disk.stash.size();
            w.failSaves(n, ghost == 1);
            String why = "free " + free + " fail save #" + n + " ghost " + ghost + " restart " + RS[r];
            int before = w.disk.stash.size();
            try {
                EmberSixMigration.claim(w, w);
                assertTrue(why + " no failure reported → no failed save", w.failedSaves == 0 || n > Math.min(free, 4)); // the n-th save was the trailing untag save
            } catch (EmberSixMigration.SaveFailed e) {
                assertTrue(w.failedSaves > 0);
                assertTrue(why + " the failed entry is still owed", w.disk.stash.size() >= stash0 - (n - 1));
            }
            assertTrue(w.disk.stash.size() <= before);
            assertEquals(why + " after the failure", f0, w.foreign());
            restart(w, r);
            assertEquals(why + " after restart", f0, w.foreign());
            for (int rep = 0; rep < 3; rep++) EmberSixMigration.claim(w, w);
            w.hardRestart();
            EmberSixMigration.claim(w, w);
            int fit = Math.min(free, 4);
            assertEquals(why, f0, w.foreign());
            assertEquals(why + " what fits is in the backpack", fit, countOrig(w));
            assertEquals(why + " the rest still waits", 4 - fit, w.disk.stash.size());
            for (String x : w.pack) assertTrue(why + " untagged", x == null || x.indexOf('#') < 0);
            scenarios++;
        }
        // settle path: a handed-out (tagged) stack whose save crashed; the next claim cannot save the player file
        for (int ghost = 0; ghost < 2; ghost++) for (int r = 0; r < 3; r++) {
            World w = world(3);
            w.migrate();
            w.pack[0] = null;
            w.persistInventory0();
            List<String> f0 = w.foreign();
            w.crashAt = w.calls + 3; // give, persist, save ← crash
            try { EmberSixMigration.claim(w, w); fail(); } catch (Crash expected) { }
            w.crashAt = -1;
            assertEquals(4, w.disk.stash.size());
            w.failSaves(1, ghost == 1);
            try { EmberSixMigration.claim(w, w); fail("settle must not drop the entry without a saved player file"); } catch (EmberSixMigration.SaveFailed expected) { }
            assertEquals("entry kept", 4, w.disk.stash.size());
            assertEquals(f0, w.foreign());
            restart(w, r);
            assertEquals(f0, w.foreign());
            EmberSixMigration.claim(w, w);
            w.hardRestart();
            EmberSixMigration.claim(w, w);
            assertEquals(f0, w.foreign());
            assertEquals(1, countOrig(w));
            assertEquals(3, w.disk.stash.size());
            scenarios++;
        }
        System.out.println("[T1-4/T1-6] claim save failure: " + scenarios + " scenarios, bad 0");
    }

    /** the 余烬-测试 fuzz, verbatim structure (seeds 1 / 42 / 1008 / 20261008 × 1500 runs × 10 ops): 0 bad */
    @Test public void qaFuzzSeeds() {
        long[] seeds = {1L, 42L, 1008L, 20261008L};
        int runs = 0, bad = 0;
        for (long seed : seeds) {
            Random rnd = new Random(seed);
            for (int run = 0; run < 1500; run++) {
                World w = world(rnd.nextInt(5));
                for (int i = 0; i < 36; i++) if (rnd.nextInt(3) == 0) w.pack[i] = null;
                w.persistInventory0();
                List<String> f0 = w.foreign();
                boolean b = false;
                for (int op = 0; op < 10; op++) {
                    w.crashAt = rnd.nextInt(3) == 0 ? w.calls + 1 + rnd.nextInt(16) : -1;
                    boolean mig = rnd.nextBoolean();
                    try { if (mig) w.migrate(); else EmberSixMigration.claim(w, w); }
                    catch (RuntimeException e) { int k = rnd.nextInt(3); if (k == 1) w.softRestart(); else if (k == 2) w.hardRestart(); }
                    w.crashAt = -1;
                    if (!f0.equals(w.foreign())) b = true;
                }
                w.hardRestart(); // the pattern the original fuzz missed: a hard kill after everything
                if (!f0.equals(w.foreign())) b = true;
                if (w.disk.flag && w.p1OnPlayer().size() != 4) b = true;
                runs++; if (b) bad++;
            }
        }
        System.out.println("[T1-4] qa fuzz: " + runs + " runs, bad " + bad);
        assertEquals(6000, runs);
        assertEquals(0, bad);
    }

    /**
     * 50,000 runs × 12 ops: migrate / claim / hard kill / graceful stop / autosave / the player throws a foreign item away, each visit
     * possibly crashed at a random call and / or with failing player-file saves (ghost or not); after every op the
     * soundness check, at the end clear → settle → hard kill → exactly one set.
     */
    @Test public void conservationFuzzWithHardKillsAndSaveFailures() {
        Random rnd = new Random(20261008L);
        int runs = 50000, ops = 0, crashes = 0, saveFails = 0, kills = 0;
        for (int run = 0; run < runs; run++) {
            World w = world(rnd.nextInt(5));
            for (int i = 0; i < 36; i++) if (rnd.nextInt(3) == 0) w.pack[i] = null;
            w.persistInventory0();
            List<String> f0 = new ArrayList<String>(w.foreign());
            StringBuilder log = new StringBuilder();
            for (int op = 0; op < 12; op++, ops++) {
                int kind = rnd.nextInt(10);
                if (kind < 7) {
                    boolean mig = kind < 4;
                    w.tamper = mig && rnd.nextInt(8) == 0 && w.charm != null && w.charm.tier > 0;
                    w.crashAt = rnd.nextInt(3) == 0 ? w.calls + 1 + rnd.nextInt(18) : -1;
                    if (rnd.nextInt(3) == 0) w.failSaves(rnd.nextInt(4) == 0 ? 1 : 1 + rnd.nextInt(4), rnd.nextBoolean());
                    int fs = w.failedSaves;
                    log.append(mig ? " mig" : " claim");
                    try {
                        log.append('=').append(mig ? String.valueOf(w.migrate()) : Arrays.toString(EmberSixMigration.claim(w, w)));
                    } catch (Crash e) {
                        crashes++;
                        int k = rnd.nextInt(3);
                        log.append(" crash/").append(RS[k]);
                        restart(w, k);
                        if (k == 2) kills++;
                    } catch (EmberSixMigration.SaveFailed e) {
                        log.append(" saveFailed");
                    }
                    if (w.failedSaves > fs) saveFails++;
                    w.tamper = false;
                    w.crashAt = -1;
                    w.clearFail();
                } else if (kind == 7) {
                    log.append(" kill"); w.hardRestart(); kills++;
                } else if (kind == 8) {
                    if (rnd.nextBoolean()) { log.append(" stop"); w.softRestart(); }
                    else { log.append(" autosave"); w.persistInventory0(); } // the server's periodic player save, at any moment
                } else { // the player throws one of their own (untagged, non-P1) items away (then an autosave)
                    int a = rnd.nextInt(36);
                    if (w.pack[a] != null && !w.pack[a].startsWith("P1:") && w.pack[a].indexOf('#') < 0) {
                        log.append(" toss ").append(w.pack[a]);
                        f0.remove(w.pack[a]);
                        w.pack[a] = null;
                        w.persistInventory0();
                    }
                }
                assertSound(w, f0, "run " + run + " op " + op + log);
            }
            settle(w, "run " + run + log);
            w.hardRestart();
            assertSound(w, f0, "run " + run + " final" + log);
            assertTrue("run " + run + log, w.disk.flag);
            assertEquals(4, w.p1OnPlayer().size());
        }
        System.out.println("[T1-4] fuzz: " + runs + " runs / " + ops + " ops (" + crashes + " crashes, " + saveFails + " failed-save visits, "
                + kills + " hard kills), bad 0");
        assertTrue(saveFails > 10000 && kills > 10000);
    }

    /** wiring: the live port reports the save outcome through a separate checked variant; savePlayerFile stays as it was */
    @Test public void liveSaveIsCheckedOnlyForSixSlot() throws java.io.IOException {
        String vault = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberVault.java")), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(vault.contains("    static void savePlayerFile(org.bukkit.entity.Player p) {\n        if (p == null || !p.isOnline()) return;\n"
                + "        try { p.saveData(); } catch (RuntimeException e) { Bukkit.getLogger().warning(\"[CoreRpg] [storage] saveData \" + p.getName() + \": \" + e); }\n    }"));
        assertTrue(vault.contains("static boolean savePlayerFileChecked("));
        String svc = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSixSlotService.java")), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(svc.contains("public boolean persistInventory() { return EmberVault.savePlayerFileChecked(p); }"));
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("savePlayerFileChecked\\(").matcher(svc);
        int n = 0; while (m.find()) n++;
        assertEquals("only the migration / claim port uses the checked save", 1, n);
    }

    /**
     * after a failed save the live inventory matches the record again, so a running server leaves nothing the player could
     * throw away and get a second time: no issued piece on the player (the originals back in their slots), no claim-tagged
     * stack (the entry still owed).
     */
    @Test public void failedSaveLeavesTheLiveInventoryAsRecorded() {
        for (int k = 0; k < 5; k++) for (int ghost = 0; ghost < 2; ghost++) {
            World w = world(k);
            String[] a0 = w.armor.clone();
            w.failSaves(1, ghost == 1);
            assertEquals(EmberSixMigration.Outcome.SAVE_FAILED, w.migrate());
            assertNotNull("journal kept", w.disk.journal);
            assertFalse(w.disk.flag);
            assertTrue("no issued piece left on the player", w.p1OnPlayer().isEmpty());
            assertArrayEquals("originals back in their slots", a0, w.armor);
            w.clearFail();
            assertEquals(EmberSixMigration.Outcome.JOURNAL_DROPPED, w.migrate());
            assertEquals(EmberSixMigration.Outcome.MIGRATED, w.migrate());
        }
        // resume of a partly worn set: the slots it filled go back to what the journal records
        World w = world(0);
        w.crashAt = 7; // after the journal and two setArmor calls
        try { w.migrate(); fail(); } catch (Crash expected) { }
        w.crashAt = -1;
        String[] live = w.armor.clone();
        w.failSaves(1, false);
        assertEquals(EmberSixMigration.Outcome.SAVE_FAILED, w.migrate());
        assertArrayEquals(live, w.armor);
        // revert whose final save fails: journal stays reverting, the next visit finishes it
        w = world(0);
        String[] a0 = w.armor.clone();
        w.tamper = true;
        w.failSaves(2, false); // the swap save works, the revert save fails
        assertEquals(EmberSixMigration.Outcome.SAVE_FAILED, w.migrate());
        assertTrue(w.disk.journal != null && w.disk.journal.reverting);
        w.tamper = false;
        w.hardRestart(); // the disk still has the swapped state
        assertEquals(EmberSixMigration.Outcome.CHECK_FAILED, w.migrate()); // revert finished
        assertNull(w.disk.journal);
        assertArrayEquals(a0, w.armor);
        assertTrue(w.p1OnPlayer().isEmpty());
        // claim
        for (int ghost = 0; ghost < 2; ghost++) {
            World c = world(3);
            c.migrate();
            c.pack[0] = null; c.pack[1] = null;
            c.persistInventory0();
            c.failSaves(2, ghost == 1); // the first hand-out saves, the second does not
            try { EmberSixMigration.claim(c, c); fail(); } catch (EmberSixMigration.SaveFailed expected) { }
            assertEquals("one claimed, the failed one still owed", 3, c.disk.stash.size());
            for (String x : c.pack) assertTrue("no tagged stack left on the player", x == null || x.indexOf('#') < 0);
            assertEquals(1, countOrig(c));
        }
    }
}
