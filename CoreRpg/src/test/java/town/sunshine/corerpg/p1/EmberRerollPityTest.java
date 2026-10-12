package town.sunshine.corerpg.p1;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import town.sunshine.corerpg.PlayerData;

/** D1248 binary remain + D12767 tip-AQ nudge-aware remain honesty. */
public class EmberRerollPityTest {

    private static EmberItemData blade() {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade",
                2, 0, 0, 3, 0, false, "drop", 1, 0);
    }

    @Test public void glance_progress_and_ready() {
        assertTrue(EmberRerollPity.glance(2, 5).contains("2"));
        assertTrue(EmberRerollPity.glance(2, 5).contains("/5"));
        assertFalse(EmberRerollPity.glance(2, 5).contains("必出"));
        assertTrue(EmberRerollPity.glance(5, 5).contains("必出"));
        assertTrue(EmberRerollPity.glance(6, 5).contains("必出"));
    }

    @Test public void remainLine_andAttempts() {
        assertEquals(3, EmberRerollPity.attemptsUntilCap(0, 3));
        assertEquals(1, EmberRerollPity.attemptsUntilCap(3, 3));
        assertEquals(1, EmberRerollPity.attemptsUntilCap(5, 3));
        assertEquals("这次必出上限档", EmberRerollPity.remainLine(3, 3));
        assertEquals("第0/3 · 含本次≤3次必出上限档", EmberRerollPity.remainLine(0, 3));
        assertEquals("第2/3 · 含本次≤1次必出上限档", EmberRerollPity.remainLine(2, 3));
        assertEquals("下次洗练必出上限档", EmberRerollPity.afterRemainLine(3, 3));
        assertEquals("含下次≤2次必出上限档", EmberRerollPity.afterRemainLine(1, 3));
    }

    @Test public void attemptsUntilCap_nudgeReady() {
        assertEquals(1, EmberRerollPity.attemptsUntilCap(3, 5, true));  // 2-1
        assertEquals(2, EmberRerollPity.attemptsUntilCap(2, 5, true));  // 3-1
        assertEquals(1, EmberRerollPity.attemptsUntilCap(4, 5, true));  // incl==1, no subtract
        assertEquals(2, EmberRerollPity.attemptsUntilCap(3, 5, false));
        assertEquals(1, EmberRerollPity.attemptsUntilCap(5, 5, true));  // already forced
        assertEquals(0, EmberRerollPity.attemptsUntilCap(0, 0, true));
    }

    @Test public void remainLine_nudgeReady_cases() {
        String r3 = EmberRerollPity.remainLine(3, 5, true);
        assertTrue(r3.contains("含本次≤1次"));
        assertTrue(r3.contains("今日加码待用"));
        assertEquals("第3/5 · 含本次≤1次必出上限档 · 今日加码待用", r3);

        String r2 = EmberRerollPity.remainLine(2, 5, true);
        assertTrue(r2.contains("含本次≤2次"));
        assertTrue(r2.contains("今日加码待用"));

        String r4 = EmberRerollPity.remainLine(4, 5, true);
        assertTrue(r4.contains("含本次≤1次"));
        assertTrue(r4.contains("今日加码待用"));

        String r3f = EmberRerollPity.remainLine(3, 5, false);
        assertEquals("第3/5 · 含本次≤2次必出上限档", r3f);
        assertFalse(r3f.contains("今日加码待用"));

        assertEquals("这次必出上限档", EmberRerollPity.remainLine(5, 5, true));
        assertFalse(EmberRerollPity.remainLine(5, 5, true).contains("今日加码待用"));

        // D1248 unary regression via binary false
        assertEquals("第0/3 · 含本次≤3次必出上限档", EmberRerollPity.remainLine(0, 3, false));
    }

    @Test public void binary_delegates_false() {
        assertEquals(EmberRerollPity.attemptsUntilCap(3, 5, false),
                EmberRerollPity.attemptsUntilCap(3, 5));
        assertEquals(EmberRerollPity.remainLine(3, 5, false),
                EmberRerollPity.remainLine(3, 5));
    }

    @Test public void hopeRerollTrack_matches_remain_whenReady() {
        PlayerData pd = new PlayerData();
        assertTrue(EmberRerollPityNudge.ready(pd));
        String expect = EmberRerollPity.remainLine(3, 5, true);
        EmberForgeHeldNext.Mats mats = new EmberForgeHeldNext.Mats(999, 999, 999, 99999);
        List<EmberHopeMeter.Track> tracks = EmberHopeMeter.priorityTracks(
                blade(), pd, mats, 3, 5, null, 0);
        EmberHopeMeter.Track reroll = null;
        for (EmberHopeMeter.Track t : tracks) if ("reroll".equals(t.id)) reroll = t;
        assertNotNull(reroll);
        assertEquals(expect, reroll.progressLine);
        assertTrue(reroll.progressLine.contains("今日加码待用"));
        assertTrue(reroll.progressLine.contains("含本次≤1次"));
    }
}
