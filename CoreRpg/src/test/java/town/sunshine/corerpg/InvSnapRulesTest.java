package town.sunshine.corerpg;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/** 1.62 inventory snapshot retention: last 50 + newest per CST day for 30 days. */
public class InvSnapRulesTest {

    private static final long NOW = 1791100800000L; // 2026-10-04 16:00 CST

    @Test public void keepsLastFiftyAndNewestPerDay() {
        List<InvSnapRules.Snap> s = new ArrayList<InvSnapRules.Snap>();
        // 40 days, 4 snapshots per day (every 6 h)
        for (int d = 0; d < 40; d++) for (int k = 0; k < 4; k++) s.add(new InvSnapRules.Snap("d" + d + "k" + k, NOW - d * InvSnapRules.DAY - k * 6 * 3600000L));
        List<String> del = InvSnapRules.prune(s, NOW, 50, 30);
        Set<String> kept = new HashSet<String>();
        for (InvSnapRules.Snap x : s) if (!del.contains(x.id)) kept.add(x.id);
        // newest 50 always kept
        for (int i = 0; i < 50; i++) assertTrue(kept.contains("d" + (i / 4) + "k" + (i % 4)));
        // one per CST day within 30 days
        Set<Long> days = new HashSet<Long>();
        for (InvSnapRules.Snap x : s) if (kept.contains(x.id)) days.add(InvSnapRules.day(x.at));
        for (int d = 0; d < 30; d++) assertTrue("day " + d, days.contains(InvSnapRules.day(NOW) - d));
        // nothing older than 30 days beyond the newest 50
        for (InvSnapRules.Snap x : s) if (InvSnapRules.day(NOW) - InvSnapRules.day(x.at) >= 30) assertFalse(x.id, kept.contains(x.id));
        assertTrue(kept.size() <= 50 + 30);
    }

    @Test public void fewSnapshotsAreNeverPruned() {
        List<InvSnapRules.Snap> s = new ArrayList<InvSnapRules.Snap>();
        for (int i = 0; i < 10; i++) s.add(new InvSnapRules.Snap("x" + i, NOW - i * 400L * InvSnapRules.DAY));
        assertTrue(InvSnapRules.prune(s, NOW, 50, 30).isEmpty());
    }

    @Test public void dayBoundaryIsCst() {
        long midnightCst = 1791043200000L; // 2026-10-04 00:00 CST = 10-03 16:00 UTC
        assertEquals(InvSnapRules.day(midnightCst), InvSnapRules.day(midnightCst + 1) );
        assertEquals(InvSnapRules.day(midnightCst) - 1, InvSnapRules.day(midnightCst - 1));
    }

    @Test public void unchangedAutomaticSnapshotsAreSkipped() {
        assertFalse(InvSnapRules.store("periodic", true));
        assertFalse(InvSnapRules.store("join", true));
        assertFalse(InvSnapRules.store("enter", true));
        assertTrue(InvSnapRules.store("periodic", false));
        assertTrue(InvSnapRules.store("death", true));
        assertTrue(InvSnapRules.store("pre-restore", true));
        assertTrue(InvSnapRules.store("manual", true));
    }

    @Test public void slotNames() {
        assertEquals("快捷栏1", InvSnapRules.slotName(0));
        assertEquals("背包1", InvSnapRules.slotName(9));
        assertEquals("头盔", InvSnapRules.slotName(39));
        assertEquals("副手", InvSnapRules.slotName(40));
    }

    @Test public void guardEngagesOnlyForConfiguredMysqlDownWithP1() {
        assertTrue(DbGuard.shouldEngage(true, false, true, true));
        assertFalse(DbGuard.shouldEngage(true, true, true, true));
        assertFalse(DbGuard.shouldEngage(false, false, true, true));
        assertFalse(DbGuard.shouldEngage(true, false, false, true));
        assertFalse(DbGuard.shouldEngage(true, false, true, false));
    }

    @Test public void gzipRoundTrip() {
        String s = "inv:\n  0: 余烬 ×1\n";
        assertEquals(s, InvSnapService.gunzip(InvSnapService.gzip(s)));
        assertEquals(40, InvSnapService.sha1(s).length());
    }
}
