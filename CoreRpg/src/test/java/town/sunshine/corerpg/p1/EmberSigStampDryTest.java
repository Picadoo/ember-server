package town.sunshine.corerpg.p1;

import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.*;

/**
 * TIP-BW D13272–D13287: stamp soft-pity glance + PAPI {@code sig_stamp_hope}.
 * SoT = {@link EmberSigStampDry#glance} — display only; zero STAMP_RATE / need / stampable edits.
 */
public class EmberSigStampDryTest {
    @Test public void expectClears() {
        assertEquals(9, EmberSigStampDry.EXPECT_CLEARS); // ceil(1/0.12)
    }
    @Test public void missAppendEmptyWhenZero() {
        assertEquals("", EmberSigStampDry.missAppend(null));
    }

    @Test public void glance_dry0_containsWeiLianKong() {
        PlayerData d = new PlayerData();
        assertEquals(0, EmberSigStampDry.get(d));
        String g = EmberSigStampDry.glance(d);
        assertTrue(g, g.contains("未连空"));
        assertFalse(g.contains("软保底已满"));
    }

    @Test public void glance_dryNeedMinus1_containsNSlashNeed() {
        PlayerData d = new PlayerData();
        int need = EmberSigStampPity.need();
        assertTrue(need >= 2);
        d.addPeriodCount(EmberSigStampDry.C_DRY, "all", need - 1);
        assertEquals(need - 1, EmberSigStampDry.get(d));
        assertFalse(EmberSigStampPity.ready(d));
        String g = EmberSigStampDry.glance(d);
        String expect = (need - 1) + "/" + need;
        assertTrue(g + " missing " + expect, g.contains(expect));
        assertFalse(g.contains("未连空"));
        assertFalse(g.contains("软保底已满"));
    }

    @Test public void glance_ready_softPityFullOrNextStamp() {
        PlayerData d = new PlayerData();
        int need = EmberSigStampPity.need();
        d.addPeriodCount(EmberSigStampDry.C_DRY, "all", need);
        assertTrue(EmberSigStampPity.ready(d));
        String g = EmberSigStampDry.glance(d);
        boolean ok = g.contains("软保底已满") || (g.contains("下次") && g.contains("盖章"));
        assertTrue(g, ok);
    }

    @Test public void papiRoutesSigStampHopeToGrowth() {
        assertEquals(EmberRunPapi.Section.GROWTH, EmberRunPapi.route("sig_stamp_hope"));
        assertEquals(EmberRunPapi.Section.GROWTH, EmberRunPapi.route("sig_stamp_dry"));
        assertEquals(EmberRunPapi.Section.GROWTH, EmberRunPapi.route("sig_dry"));
        assertEquals(EmberRunPapi.Section.GROWTH, EmberRunPapi.route("sig_chase_hope"));
    }

    @Test public void sigPapiWiresStampHopeToGlance() throws Exception {
        String s = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberGrowthService.java")),
                java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(s.contains("\"stamp_hope\".equals(key)"));
        assertTrue(s.contains("\"stamp_dry\".equals(key)"));
        assertTrue(s.contains("return EmberSigStampDry.glance(d)"));
        // aliases share one glance; no second dry counter
        assertFalse(s.contains("p1_sig_stamp_dry2"));
    }
}
