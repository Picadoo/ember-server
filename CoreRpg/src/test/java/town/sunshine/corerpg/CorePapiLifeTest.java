package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static town.sunshine.corerpg.CorePapi.Section.LIFE;

/** D388: life_* PAPI conventions (Bukkit-free via CorePapiLife.apply). */
public final class CorePapiLifeTest {

    @Test public void midLevelSoulAndHatch() {
        // Lv.3, xp=80, next=150; soul used 1/4; hatch used 0/2
        assertEquals("3", CorePapiLife.apply("life_level", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("80", CorePapiLife.apply("life_xp", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("150", CorePapiLife.apply("life_xp_next", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("生活 Lv.3（经验 80/150）",
                CorePapiLife.apply("life_level_line", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("3", CorePapiLife.apply("life_soul_daily_left", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("今日兑尘 已用 1/4 · 剩 3",
                CorePapiLife.apply("life_soul_daily_line", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("2", CorePapiLife.apply("life_hatch_weekly_left", 3, 80, 150, 1, 4, 0, 2));
        assertEquals("本周孵化 已用 0/2 · 剩 2",
                CorePapiLife.apply("life_hatch_weekly_line", 3, 80, 150, 1, 4, 0, 2));
    }

    @Test public void maxLevelAndCapsExhausted() {
        assertEquals("0", CorePapiLife.apply("life_xp_next", 8, 1600, 0, 4, 4, 2, 2));
        assertEquals("生活 Lv.8（已满级）",
                CorePapiLife.apply("life_level_line", 8, 1600, 0, 4, 4, 2, 2));
        assertEquals("0", CorePapiLife.apply("life_soul_daily_left", 8, 1600, 0, 4, 4, 2, 2));
        assertEquals("今日兑尘 已用 4/4 · 剩 0",
                CorePapiLife.apply("life_soul_daily_line", 8, 1600, 0, 4, 4, 2, 2));
        assertEquals("0", CorePapiLife.apply("life_hatch_weekly_left", 8, 1600, 0, 4, 4, 2, 2));
        assertEquals("本周孵化 已用 2/2 · 剩 0",
                CorePapiLife.apply("life_hatch_weekly_line", 8, 1600, 0, 4, 4, 2, 2));
    }

    @Test public void overCapClampsLeftToZero() {
        assertEquals("0", CorePapiLife.apply("life_soul_daily_left", 2, 20, 60, 9, 4, 0, 2));
        assertEquals("今日兑尘 已用 4/4 · 剩 0",
                CorePapiLife.apply("life_soul_daily_line", 2, 20, 60, 9, 4, 0, 2));
    }

    @Test public void levelFloorAtOne() {
        assertEquals("1", CorePapiLife.apply("life_level", 0, 0, 20, 0, 4, 0, 2));
        assertEquals("生活 Lv.1（经验 0/20）",
                CorePapiLife.apply("life_level_line", 0, 0, 20, 0, 4, 0, 2));
    }

    @Test public void unknownKeyNull() {
        assertNull(CorePapiLife.apply("life_nosuch", 1, 0, 20, 0, 4, 0, 2));
        assertNull(CorePapiLife.apply(null, 1, 0, 20, 0, 4, 0, 2));
    }

    @Test public void resolveNullServiceUsesEmptyConventions() {
        assertEquals("生活 Lv.1（经验 0/20）",
                CorePapiLife.resolve(null, new PlayerData(), "life_level_line"));
        assertEquals("4", CorePapiLife.resolve(null, new PlayerData(), "life_soul_daily_left"));
        assertEquals("2", CorePapiLife.resolve(null, new PlayerData(), "life_hatch_weekly_left"));
        assertNull(CorePapiLife.resolve(null, new PlayerData(), "not_a_life_key"));
    }

    @Test public void capsPinned() {
        assertEquals(8, CorePapi.LIFE.size());
        assertEquals(4, CorePapiLife.SOUL_DAILY_CAP);
        assertEquals(2, CorePapiLife.HATCH_WEEKLY_CAP);
    }

    @Test public void everyLifeKeyRoutes() {
        for (String k : CorePapi.LIFE) {
            assertEquals(k, LIFE, CorePapi.route(k));
        }
    }
}
