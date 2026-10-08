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
    static final class World implements EmberSixMigration.Port<String>, EmberSixMigration.ClaimPort<String> {
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
        void hardRestart() { armor = diskArmor.clone(); pack = diskPack.clone(); crashAt = -1; }
        /** graceful stop: the server saves the player on the way down */
        void softRestart() { persistInventory0(); crashAt = -1; }

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
        public void persistInventory() { tick(); persistInventory0(); }
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
        public void untagExcept(Set<String> owed) {
            for (int i = 0; i < 36; i++) {
                String s = pack[i];
                if (s == null || s.indexOf('#') < 0) continue;
                String id = s.substring(s.indexOf('#') + 1);
                if (!owed.contains(id)) pack[i] = s.substring(0, s.indexOf('#'));
            }
        }

        String detail = "";
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
}
