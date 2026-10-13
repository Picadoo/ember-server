package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TIP-CH D13313: today_triple — 优先今日推有奖 / 次回周目标 / 再事件 / 全空 / ok·kind 边界。
 */
public class EmberTodayTripleTest {

    @Test
    public void preferTodayWhenUnlockedWithDayLeft() {
        EmberTodayTriple.Pick p = EmberTodayTriple.pick("sx03", true, 2, false, "sx01", "q05");
        assertEquals(EmberTodayTriple.KIND_TODAY, p.kind);
        assertEquals("sx03", p.mapKey);
        assertEquals(EmberTodayTriple.ALIAS_TODAY, p.alias);
        assertEquals("1", EmberTodayTriple.ok(p));
    }

    @Test
    public void todaySkippedWhenLockedOrNoDayLeft() {
        EmberTodayTriple.Pick locked = EmberTodayTriple.pick("sx03", false, 3, false, "sx01", "q05");
        assertEquals(EmberTodayTriple.KIND_WEEK, locked.kind);
        assertEquals("sx01", locked.mapKey);

        EmberTodayTriple.Pick full = EmberTodayTriple.pick("sx03", true, 0, false, "sx02", "q05");
        assertEquals(EmberTodayTriple.KIND_WEEK, full.kind);
        assertEquals("sx02", full.mapKey);
    }

    @Test
    public void weekWhenNotDoneAndChaseOk() {
        EmberTodayTriple.Pick p = EmberTodayTriple.pick("sx03", true, 0, false, "sx07", "q03");
        assertEquals(EmberTodayTriple.KIND_WEEK, p.kind);
        assertEquals("sx07", p.mapKey);
        assertEquals(EmberTodayTriple.ALIAS_WEEK, p.alias);
        assertEquals("1", EmberTodayTriple.ok(p));
    }

    @Test
    public void weekSkippedWhenDoneOrNoChase() {
        EmberTodayTriple.Pick done = EmberTodayTriple.pick("sx01", false, 0, true, "sx01", "q04");
        assertEquals(EmberTodayTriple.KIND_EVENT, done.kind);
        assertEquals("q04", done.mapKey);

        EmberTodayTriple.Pick noChase = EmberTodayTriple.pick("sx01", false, 0, false, "", "q04");
        assertEquals(EmberTodayTriple.KIND_EVENT, noChase.kind);
        assertEquals("q04", noChase.mapKey);

        EmberTodayTriple.Pick nullChase = EmberTodayTriple.pick("sx01", false, 0, false, null, "q02");
        assertEquals(EmberTodayTriple.KIND_EVENT, nullChase.kind);
        assertEquals("q02", nullChase.mapKey);
    }

    @Test
    public void eventWhenChaseOk() {
        EmberTodayTriple.Pick p = EmberTodayTriple.pick("sx01", false, 0, true, "", "q07");
        assertEquals(EmberTodayTriple.KIND_EVENT, p.kind);
        assertEquals("q07", p.mapKey);
        assertEquals(EmberTodayTriple.ALIAS_EVENT, p.alias);
        assertEquals("1", EmberTodayTriple.ok(p));
    }

    @Test
    public void allEmpty() {
        EmberTodayTriple.Pick p = EmberTodayTriple.pick("sx01", false, 0, true, "", "");
        assertEquals(EmberTodayTriple.KIND_EMPTY, p.kind);
        assertEquals("", p.mapKey);
        assertEquals("", p.alias);
        assertFalse(p.present());
        assertEquals("0", EmberTodayTriple.ok(p));

        EmberTodayTriple.Pick blankToday = EmberTodayTriple.pick("", true, 3, true, null, null);
        assertEquals(EmberTodayTriple.KIND_EMPTY, blankToday.kind);
        assertEquals("0", EmberTodayTriple.ok(blankToday));
    }

    @Test
    public void okAndKindBoundary() {
        assertEquals("0", EmberTodayTriple.ok((EmberTodayTriple.Pick) null));
        assertEquals("0", EmberTodayTriple.ok(""));
        assertEquals("0", EmberTodayTriple.ok((String) null));
        assertEquals("1", EmberTodayTriple.ok("sx01"));
        assertEquals("1", EmberTodayTriple.ok("q03"));

        EmberTodayTriple.Pick today = EmberTodayTriple.pick("sx05", true, 1, false, "sx01", "q01");
        assertTrue(today.present());
        assertEquals(EmberTodayTriple.KIND_TODAY, today.kind);

        // week done + no event → empty even if weekChase would have been set
        EmberTodayTriple.Pick empty = EmberTodayTriple.pick(null, true, 5, true, "sx09", "");
        assertEquals(EmberTodayTriple.KIND_EMPTY, empty.kind);
        assertEquals("0", EmberTodayTriple.ok(empty));
    }

    @Test
    public void priorityTodayBeatsWeekAndEvent() {
        EmberTodayTriple.Pick p = EmberTodayTriple.pick("sx02", true, 1, false, "sx09", "q01");
        assertEquals("sx02", p.mapKey);
        assertEquals(EmberTodayTriple.KIND_TODAY, p.kind);
    }

    @Test
    public void priorityWeekBeatsEvent() {
        EmberTodayTriple.Pick p = EmberTodayTriple.pick("sx02", true, 0, false, "sx09", "q01");
        assertEquals("sx09", p.mapKey);
        assertEquals(EmberTodayTriple.KIND_WEEK, p.kind);
    }
}
