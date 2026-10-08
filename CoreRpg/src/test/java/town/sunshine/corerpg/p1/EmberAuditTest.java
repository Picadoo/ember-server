package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class EmberAuditTest {
    static boolean anyStarts(List<String> d, String p) { for (String l : d) if (l.startsWith(p)) return true; return false; }
    static boolean anyContains(List<String> d, String p) { for (String l : d) if (l.contains(p)) return true; return false; }

    @Test
    public void diffFindsMissingRowsLostItemsAndDuplicates() {
        Map<String, Integer> held = new HashMap<String, Integer>();
        held.put("a", 1); held.put("b", 2); held.put("c", 1); held.put("x", 1);
        Map<String, String> rows = new HashMap<String, String>();
        rows.put("a", "active"); rows.put("b", "active"); rows.put("c", "dismantled");
        rows.put("lost", "active"); rows.put("gone", "dismantled");
        assertEquals(Arrays.asList("DUPLICATE b x2", "HELD_NOT_ACTIVE c state=dismantled", "NO_ROW x", "ACTIVE_NOT_HELD lost"),
                EmberAudit.diff(held, rows));
    }

    @Test
    public void cleanInventoryHasNoFindings() {
        Map<String, Integer> held = new HashMap<String, Integer>();
        held.put("a", 1);
        Map<String, String> rows = new HashMap<String, String>();
        rows.put("a", "active"); rows.put("old", "exchanged");
        assertEquals(0, EmberAudit.diff(held, rows).size());
    }

    /** D321 H7: a uid only in 待领 is held for audit — never ACTIVE_NOT_HELD; OP sees ACTIVE_IN_STASH (勿 restore) */
    @Test
    public void stashCountsAsHeldNeverActiveNotHeld_D321() {
        Map<String, Integer> live = new HashMap<String, Integer>();
        live.put("worn", 1);
        Set<String> stash = new HashSet<String>(Arrays.asList("df5cb4bb-companion-boots", "r-reissue-uid"));
        Map<String, String> rows = new HashMap<String, String>();
        rows.put("worn", "active");
        rows.put("df5cb4bb-companion-boots", "active"); // H7: only in 待领
        rows.put("r-reissue-uid", "active");            // re-entry "r"+uid 待领
        rows.put("truly-lost", "active");
        rows.put("retired-one", "retired");
        List<String> d = EmberAudit.diff(live, stash, rows);
        assertFalse(d.toString(), anyStarts(d, "ACTIVE_NOT_HELD df5cb4bb") || anyStarts(d, "ACTIVE_NOT_HELD r-reissue"));
        assertTrue(d.toString(), anyStarts(d, "ACTIVE_IN_STASH df5cb4bb-companion-boots") && anyContains(d, EmberAudit.IN_STASH_TEXT));
        assertTrue(d.toString(), anyStarts(d, "ACTIVE_IN_STASH r-reissue-uid"));
        assertTrue(d.contains("ACTIVE_NOT_HELD truly-lost"));
        // 待领 does not inflate DUPLICATE when the same uid is also live (claim settle window)
        live.put("df5cb4bb-companion-boots", 1);
        d = EmberAudit.diff(live, stash, rows);
        assertFalse("stash must not add a second copy toward DUPLICATE", anyStarts(d, "DUPLICATE df5cb4bb"));
        assertFalse("live+stash same uid is not ACTIVE_*", anyContains(d, "df5cb4bb"));
        // after claim: live has it, stash empty → clean for that uid; same uid still ≤1
        d = EmberAudit.diff(live, Collections.<String>emptySet(), rows);
        assertEquals(1, (int) live.get("df5cb4bb-companion-boots"));
        assertFalse(d.toString(), anyContains(d, "df5cb4bb"));
        assertTrue(d.contains("ACTIVE_NOT_HELD truly-lost"));
        assertTrue(d.contains("ACTIVE_NOT_HELD r-reissue-uid"));
    }

    /** D321 H7: restore refuses when the uid is in 待领, with the OP-facing 待领 wording */
    @Test
    public void restoreSkipsWhenUidInStash_D321() {
        assertEquals(EmberAudit.RESTORE_IN_STASH, EmberAudit.restoreBlockReason(true, false));
        assertEquals(EmberAudit.RESTORE_IN_STASH, EmberAudit.restoreBlockReason(true, true)); // stash wording wins
        assertEquals("该 uid 仍有在线副本，不补发（防复制）", EmberAudit.restoreBlockReason(false, true));
        assertNull(EmberAudit.restoreBlockReason(false, false));
        assertTrue(EmberAudit.RESTORE_IN_STASH.contains("待领"));
        assertTrue(EmberAudit.RESTORE_IN_STASH.contains("勿补发"));
        assertTrue(EmberAudit.IN_STASH_TEXT.contains("勿 audit restore"));
    }

    /** wiring: heldLive + stashP1Uids feed diff; restore uses restoreBlockReason before minting */
    @Test
    public void auditWiresStashIntoHeldAndRestore_D321() throws Exception {
        String audit = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberAudit.java")), java.nio.charset.StandardCharsets.UTF_8);
        String svc = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberSixSlotService.java")), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(svc.contains("public java.util.Set<String> stashP1Uids(UUID id)"));
        assertTrue(svc.contains("for (EmberSixMigration.StashEntry<ItemStack> e : load(id).stash.values())"));
        assertTrue(audit.contains("diff(live, stash, st)"));
        assertTrue(audit.contains("stashUids(p.getUniqueId())"));
        assertTrue(audit.contains("six.stashP1Uids(id)"));
        int rest = audit.indexOf("private boolean restore(");
        String body = audit.substring(rest, audit.indexOf("p.getInventory().addItem(it)", rest));
        assertTrue(body.contains("restoreBlockReason(inStash, liveAnywhere)"));
        assertTrue(body.indexOf("inStash") < body.indexOf("addItem") || body.contains("restoreBlockReason"));
        assertTrue(body.contains("stashUids(p.getUniqueId()).contains(uid)"));
        assertFalse("must not mint before the stash check", body.indexOf("restoreBlockReason") < 0);
        // ACTIVE_IN_STASH before ACTIVE_NOT_HELD in the pure branch
        int a = audit.indexOf("ACTIVE_IN_STASH"), b = audit.indexOf("ACTIVE_NOT_HELD \" + e.getKey()");
        assertTrue(a > 0 && b > a);
    }
}
