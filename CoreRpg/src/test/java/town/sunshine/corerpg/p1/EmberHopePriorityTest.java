package town.sunshine.corerpg.p1;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import town.sunshine.corerpg.PlayerData;

/**
 * D12241–D12243: hope-glance priority track (display only). Does not touch pity math tests.
 */
public class EmberHopePriorityTest {

    private static EmberItemData blade(int enh, int pity, int quality, int craft) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade",
                2, quality, craft, enh, pity, false, "drop", 1, 0);
    }

    private static EmberForgeHeldNext.Mats matsOk() {
        return new EmberForgeHeldNext.Mats(999, 999, 999, 99999);
    }

    @Test
    public void emptyHand_fallbackContains稳养句() {
        PlayerData pd = new PlayerData();
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                null, pd, matsOk(), 0, 5, null, 0);
        assertEquals(1, tracks.size());
        EmberHopeMeter.Track t = tracks.get(0);
        assertEquals("fallback", t.id);
        assertTrue(t.progressLine.contains("今天可稳养"));
        assertTrue(EmberHopeMeter.chatLines(t).get(0).contains("稳养"));
    }

    @Test
    public void priority_skipsFullEnhance() {
        PlayerData pd = new PlayerData();
        EmberItemData full = blade(EmberTables.MAX_ENHANCE, 0, 0, 0);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                full, pd, matsOk(), 0, 5, null, 0);
        for (EmberHopeMeter.Track t : tracks) {
            assertFalse("enhance must be skipped when full", "enhance".equals(t.id));
        }
        assertEquals("quality", tracks.get(0).id);
        assertTrue(tracks.get(0).forgeHint.contains("成色"));
    }

    @Test
    public void priority_enhanceFirstWhenEligible() {
        PlayerData pd = new PlayerData();
        EmberItemData d = blade(3, 0, 0, 0);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 0, 5, null, 0);
        assertEquals("enhance", tracks.get(0).id);
        assertEquals("点强化", tracks.get(0).forgeHint);
    }

    @Test
    public void cycle_indexWraps() {
        PlayerData pd = new PlayerData();
        EmberItemData d = blade(3, 0, 0, 0);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 0, 5, null, 0);
        int n = tracks.size();
        assertTrue(n >= 2);
        assertEquals(0, EmberHopeMeter.hopeIndex(pd));
        assertEquals(tracks.get(0).id, EmberHopeMeter.current(pd, tracks).id);
        for (int i = 0; i < n - 1; i++) {
            int next = EmberHopeMeter.cycle(pd, n);
            assertEquals(i + 1, next);
        }
        int wrapped = EmberHopeMeter.cycle(pd, n);
        assertEquals(0, wrapped);
        assertEquals(0, EmberHopeMeter.hopeIndex(pd));
        assertEquals(tracks.get(0).id, EmberHopeMeter.current(pd, tracks).id);
    }

    @Test
    public void chatLines_threeRows() {
        List<String> lines = EmberHopeMeter.chatLines(EmberHopeMeter.FALLBACK);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).startsWith("§e希望 · "));
        assertTrue(lines.get(1).contains("今天可稳养"));
        assertTrue(lines.get(2).contains("打开工坊"));
    }

    @Test
    public void openTell_isProgressOnly() {
        assertEquals(EmberHopeMeter.FALLBACK.progressLine,
                EmberHopeMeter.openTellLine(EmberHopeMeter.FALLBACK));
    }

    @Test
    public void qualitySkippedAtForgeCap() {
        PlayerData pd = new PlayerData();
        EmberItemData d = blade(3, 0, EmberHopeMeter.QUALITY_FORGE_CAP, 0);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 0, 5, null, 0);
        for (EmberHopeMeter.Track t : tracks) {
            assertFalse("quality".equals(t.id));
        }
    }

    @Test
    public void refineSkippedWhenFull() {
        PlayerData pd = new PlayerData();
        EmberItemData d = blade(3, 0, 0, EmberTables.MAX_CRAFT);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 0, 5, null, 0);
        for (EmberHopeMeter.Track t : tracks) {
            assertFalse("refine".equals(t.id));
        }
    }

    @Test
    public void rerollOnlyWhenProgress() {
        PlayerData pd = new PlayerData();
        EmberItemData d = blade(3, 0, 0, 0);
        List<EmberHopeMeter.Track> no = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 0, 5, null, 0);
        for (EmberHopeMeter.Track t : no) assertFalse("reroll".equals(t.id));
        List<EmberHopeMeter.Track> yes = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 2, 5, null, 0);
        boolean found = false;
        for (EmberHopeMeter.Track t : yes) if ("reroll".equals(t.id)) found = true;
        assertTrue(found);
    }

    @Test
    public void chatLines_gemOpensGemPage() {
        EmberHopeMeter.Track gem = new EmberHopeMeter.Track(
                "gem", "宝石通关保底",
                EmberGemAcquire.remainLine(2),
                "材料够", "通关攒石", EmberForgeGoal.GEM);
        List<String> lines = EmberHopeMeter.chatLines(gem);
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("宝石通关保底"));
        assertTrue(lines.get(1).contains("通关保底"));
        assertTrue(lines.get(2).contains("打开宝石页"));
        assertFalse(lines.get(2).contains("打开工坊"));
    }

    @Test
    public void gemTrack_progressUsesRemainLine() {
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberGemAcquire.C_PITY, "all", 2);
        EmberItemData d = blade(3, 0, 0, 0);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                d, pd, matsOk(), 0, 5, null, 0);
        EmberHopeMeter.Track gem = null;
        for (EmberHopeMeter.Track t : tracks) if ("gem".equals(t.id)) gem = t;
        assertNotNull(gem);
        assertEquals(EmberGemAcquire.remainLine(2), gem.progressLine);
        assertTrue(gem.progressLine.contains("再通关3次得石"));
    }
}
