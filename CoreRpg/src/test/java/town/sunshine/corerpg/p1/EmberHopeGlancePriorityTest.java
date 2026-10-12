package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import town.sunshine.corerpg.PlayerData;

/** D12241: priority track order / skip full enhance / empty-hand fallback. Zero pity math change. */
public class EmberHopeGlancePriorityTest {

    private static EmberItemData bladeEnh(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 1, 0);
    }

    private static EmberItemData bladeFull() {
        // enhance 10 = full; quality 2 = forge cap; craft 3 = max
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 2, 3, 10, 0, false, "drop", 1, 0);
    }

    @Test public void emptyHand_fallbackFirst() {
        PlayerData pd = new PlayerData();
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                null, pd, new EmberForgeHeldNext.Mats(0, 0, 0, 0), 0, 5, null, 0);
        assertEquals(1, tracks.size());
        assertEquals("fallback", tracks.get(0).id);
        assertTrue(tracks.get(0).progressLine.contains("今天可稳养"));
        EmberHopeMeter.Track cur = EmberHopeMeter.current(pd, tracks);
        assertTrue(cur.progressLine.contains("今天可稳养"));
    }

    @Test public void fullEnhance_skipped() {
        PlayerData pd = new PlayerData();
        EmberItemData full = bladeFull();
        assertTrue(EmberHopeMeter.enhance(full).contains("已满"));
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                full, pd, new EmberForgeHeldNext.Mats(999, 999, 999, 9999), 0, 5, null, 0);
        for (EmberHopeMeter.Track t : tracks) {
            assertFalse("enhance must be skipped when full", "enhance".equals(t.id));
            assertFalse("quality at forge cap skipped", "quality".equals(t.id));
            assertFalse("refine full skipped", "refine".equals(t.id));
        }
        // still has account tracks
        boolean hasDq = false, hasGem = false;
        for (EmberHopeMeter.Track t : tracks) {
            if ("drop_q".equals(t.id)) hasDq = true;
            if ("gem".equals(t.id)) hasGem = true;
        }
        assertTrue(hasDq && hasGem);
    }

    @Test public void enhanceFirst_whenHeldUpgradeable() {
        PlayerData pd = new PlayerData();
        EmberItemData mid = bladeEnh(3, 0);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                mid, pd, new EmberForgeHeldNext.Mats(0, 0, 0, 0), 0, 5, null, 0);
        assertEquals("enhance", tracks.get(0).id);
        assertTrue(tracks.get(0).progressLine.length() > 0);
        assertEquals(3, EmberHopeMeter.chatLines(tracks.get(0)).size());
    }

    @Test public void cycle_advancesSoftIndex() {
        PlayerData pd = new PlayerData();
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                bladeEnh(3, 0), pd, new EmberForgeHeldNext.Mats(0, 0, 0, 0), 2, 5, null, 0);
        assertTrue(tracks.size() >= 2);
        assertEquals(0, EmberHopeMeter.hopeIndex(pd) % tracks.size());
        int n = EmberHopeMeter.cycle(pd, tracks.size());
        assertEquals(1, n);
        assertEquals(tracks.get(1).id, EmberHopeMeter.current(pd, tracks).id);
    }

    @Test public void openTell_oneLine() {
        assertEquals(EmberHopeMeter.FALLBACK.progressLine,
                EmberHopeMeter.openTellLine(EmberHopeMeter.FALLBACK));
        assertTrue(EmberHopeMeter.openTellLine(EmberHopeMeter.FALLBACK).contains("今天可稳养"));
    }

    @Test public void menuNow_form() {
        String line = EmberHopeMeter.menuNow(EmberHopeMeter.FALLBACK);
        assertTrue(line.startsWith("§e"));
        assertTrue(line.contains("稳养"));
        assertTrue(line.contains("§f"));
        assertTrue(line.contains("今天可稳养"));
        // overload resolves current (empty hand → fallback)
        PlayerData pd = new PlayerData();
        String live = EmberHopeMeter.menuNow(null, pd, new EmberForgeHeldNext.Mats(0, 0, 0, 0), 0, 5, null, 0);
        assertEquals(line, live);
    }

    @Test public void menuNow_usesCurrentTitleAndProgress() {
        PlayerData pd = new PlayerData();
        EmberItemData mid = bladeEnh(3, 0);
        EmberHopeMeter.Track t = EmberHopeMeter.current(mid, pd,
                new EmberForgeHeldNext.Mats(0, 0, 0, 0), 0, 5, null, 0, null);
        String line = EmberHopeMeter.menuNow(t);
        assertTrue(line.contains(t.title));
        assertTrue(line.contains(t.progressLine.replace("§7", "").substring(0, 4)) || line.contains(t.progressLine));
        assertEquals("§e" + t.title + " §f" + t.progressLine, line);
    }
}
