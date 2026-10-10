package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.encounter.CounterplayKind;
import town.sunshine.corerpg.p1.encounter.EmberCounterplay;

import org.junit.Test;
import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * D236: pins existing D188 / D192 / D193 counterplay behaviour on the EmberCounterplay adapter
 * (same predicates Director used to own inline). Numbers / caps unchanged (bv58).
 */
public class EmberCounterplayTest {

    private static EmberRunMaps.Skill skill(Object... kv) {
        Map<String, Object> m = new HashMap<String, Object>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return new EmberRunMaps.Skill(m);
    }

    @Test public void armWhiffOnlyWhenStunAndSomeoneInside_D192() {
        assertTrue(EmberCounterplay.armWhiff(0.5, true));
        assertFalse(EmberCounterplay.armWhiff(0.5, false)); // far away ≠ dodge
        assertFalse(EmberCounterplay.armWhiff(0, true));
        assertFalse(EmberCounterplay.armWhiff(-1, true));
    }

    @Test public void armBreakNeedFloorsAtOneAndRespectsZero_D193() {
        assertEquals(0, EmberCounterplay.armBreakNeed(1000, 0), 0);
        assertEquals(65, EmberCounterplay.armBreakNeed(1000, 0.065), 1e-9);
        assertEquals(1.0, EmberCounterplay.armBreakNeed(10, 0.065), 0); // floor 1 HP
        assertEquals(500, EmberCounterplay.armBreakNeed(1000, 0.5), 0);
    }

    @Test public void armWallCrashNeedsStunAndWall_D188() {
        assertTrue(EmberCounterplay.armWallCrash(1.5, true));
        assertFalse(EmberCounterplay.armWallCrash(1.5, false));
        assertFalse(EmberCounterplay.armWallCrash(0, true));
    }

    @Test public void whiffsMatchesDirectorContract_D192() {
        EmberRunMaps.Skill sk = skill("type", "cone", "whiff_stun", 0.5);
        assertTrue(EmberCounterplay.whiffs(true, 0, sk));
        assertFalse(EmberCounterplay.whiffs(true, 1, sk));
        assertFalse(EmberCounterplay.whiffs(false, 0, sk));
        assertFalse(EmberCounterplay.whiffs(true, 0, skill("type", "cone")));
        assertFalse(EmberCounterplay.whiffs(true, 0, (EmberRunMaps.Skill) null));
        assertTrue(EmberCounterplay.whiffs(true, 0, 0.5));
        assertFalse(EmberCounterplay.whiffs(true, 0, 0));
    }

    @Test public void brokenOnlyWhenArmedAndNeedReached_D193() {
        assertTrue(EmberCounterplay.broken(200, 200));
        assertTrue(EmberCounterplay.broken(200, 250));
        assertFalse(EmberCounterplay.broken(200, 199.9));
        assertFalse(EmberCounterplay.broken(0, 500));
    }

    @Test public void crashGridSameAsFormerDirector_D188() {
        EmberCounterplay.GroundTest wall = new EmberCounterplay.GroundTest() {
            public boolean ok(double x, double z) { return z < 41 || x >= 6; }
        };
        double h = EmberCounterplay.BOSS_HALF_WIDTH;
        assertTrue(EmberCounterplay.crashGrid(wall, 0.5, 46.5, 0, -1, 5.0, 8, h));
        assertFalse(EmberCounterplay.crashGrid(wall, 0.5, 46.5, 0, -1, 8.0, 8, h)); // full length → no crash
        assertFalse(EmberCounterplay.crashGrid(wall, 0.5, 46.5, -1, 0, 5.0, 8, h)); // sideways open
        assertFalse(EmberCounterplay.crashGrid(wall, 0.5, 52.5, 0, 1, 3.0, 8, h)); // away from wall
    }

    @Test public void clearRunGridBoxEdges_B2165() {
        EmberCounterplay.GroundTest hall = new EmberCounterplay.GroundTest() {
            public boolean ok(double x, double z) { return z >= 41 && x < 6 && x >= -5; }
        };
        assertEquals(5.0, EmberCounterplay.clearRunGrid(hall, 0.5, 46.5, 0, -1, 8, EmberCounterplay.BOSS_HALF_WIDTH), 1e-9);
        assertEquals(5.5, EmberCounterplay.clearRunGrid(hall, 0.5, 46.5, 0, -1, 8, 0), 1e-9);
        assertTrue(EmberCounterplay.CHARGE_MIN >= 2.0);
    }

    @Test public void stunTimingPreservesDirectorFormula() {
        assertEquals(0L, EmberCounterplay.stunMs(0));
        assertEquals(1500L, EmberCounterplay.stunMs(1.5));
        assertEquals(500L, EmberCounterplay.stunMs(0.5));
        assertEquals(0, EmberCounterplay.stunPotionTicks(0));
        assertEquals(34, EmberCounterplay.stunPotionTicks(1.5)); // 1.5*20+4
        assertEquals(14, EmberCounterplay.stunPotionTicks(0.5));
        long[] b = EmberCounterplay.applyStunBounds(10000L, 1500L, 11000L, 10500L);
        assertEquals(11500L, b[0]); // stunUntil
        assertEquals(11500L, b[1]); // recoverUntil maxed
        assertEquals(11500L, b[2]); // followStart maxed
        long[] b2 = EmberCounterplay.applyStunBounds(10000L, 500L, 12000L, 13000L);
        assertEquals(10500L, b2[0]);
        assertEquals(12000L, b2[1]); // recover already later
        assertEquals(13000L, b2[2]); // follow already later
    }

    @Test public void hintSuffixesMatchLiveWarnLines() {
        assertEquals("", EmberCounterplay.wallHint(0, "1.5"));
        assertEquals(" §a· 让它撞上墙会晕 1.5 秒", EmberCounterplay.wallHint(1.5, "1.5"));
        // D304 W1c: whiff key always hints (armed ignored); honest 先踩进圈再躲开
        assertEquals("", EmberCounterplay.whiffHint(0, true, "0.5"));
        assertEquals(" §a· 先踩进圈再躲开会踉跄 0.5 秒", EmberCounterplay.whiffHint(0.5, false, "0.5"));
        assertEquals(" §a· 先踩进圈再躲开会踉跄 0.5 秒", EmberCounterplay.whiffHint(0.5, true, "0.5"));
        assertTrue(EmberCounterplay.breakHint(65, 0.5, "0.5").contains("65"));
        assertTrue(EmberCounterplay.breakHint(65, 0.5, "0.5").contains("可打断"));
        assertEquals("", EmberCounterplay.breakHint(0, 0.5, "0.5"));
    }

    @Test public void castStartBar_keyGatedSuffixes() {
        assertEquals("圆形", EmberCounterplay.shapeShort("circle"));
        assertEquals("冲撞", EmberCounterplay.shapeShort("charge"));
        String bar = EmberCounterplay.castStartBar("重斩", "cone", 1.0, 0, 0.5, 0, false);
        assertTrue(bar.contains("«重斩»"));
        assertTrue(bar.contains("扇形"));
        assertTrue(bar.contains("1s"));
        assertTrue(bar.contains("先踩再躲"));
        assertFalse(bar.contains("撞墙"));
        assertFalse(bar.contains("可破招"));
        String share = EmberCounterplay.castStartBar("烬核", "circle", 3.0, 0, 0, 0, true);
        assertTrue(share.contains("靠拢分摊"));
        String wall = EmberCounterplay.castStartBar("冲撞", "charge", 1.3, 1.5, 0, 0, false);
        assertTrue(wall.contains("撞墙"));
        String brk = EmberCounterplay.castStartBar("霜潮汲取", "circle", 3.0, 0, 0, 65, false);
        assertTrue(brk.contains("可破招"));
    }

    @Test public void kindOfReadsParsedSkillFields() {
        assertEquals("WALL", EmberCounterplay.kindOf(skill("type", "charge", "wall_stun", 1.5)));
        assertEquals("WHIFF", EmberCounterplay.kindOf(skill("type", "cone", "whiff_stun", 0.5)));
        assertEquals("BREAK", EmberCounterplay.kindOf(skill("type", "circle", "break_hp", 0.065)));
        assertEquals("NONE", EmberCounterplay.kindOf(skill("type", "cone")));
        assertEquals("NONE", EmberCounterplay.kindOf(null));
    }

    @Test public void directorDelegatesStillAgree() {
        // thin Director delegates must stay behaviour-identical (EmberRunShapeTest also pins these)
        EmberRunMaps.Skill sk = skill("type", "cone", "whiff_stun", 1.0);
        assertEquals(EmberRunDirector.whiffs(true, 0, sk),
                EmberCounterplay.whiffs(true, 0, sk));
        assertEquals(EmberRunDirector.broken(100, 100),
                EmberCounterplay.broken(100, 100));
        assertEquals(EmberCounterplay.BOSS_HALF_WIDTH, EmberRunDirector.BOSS_HALF_WIDTH, 0);
        assertEquals(EmberCounterplay.CHARGE_MIN, EmberRunDirector.CHARGE_MIN, 0);
    }

    @Test public void firstFlash_D283_shortVerbs() {
        assertTrue(EmberCounterplay.firstFlash(CounterplayKind.WALL).contains("撞墙破绽"));
        assertTrue(EmberCounterplay.firstFlash(CounterplayKind.WHIFF).contains("落空破绽"));
        assertTrue(EmberCounterplay.firstFlash(CounterplayKind.BREAK).contains("破招成功"));
        assertEquals("", EmberCounterplay.firstFlash(null));
    }

    @Test public void noteCounterplay_D283_firstOnly() {
        EmberRunSession s = new EmberRunSession();
        assertTrue(s.noteCounterplay(CounterplayKind.WALL));
        assertFalse(s.noteCounterplay(CounterplayKind.WALL));
        assertEquals(2, s.wallHits);
        assertTrue(s.noteCounterplay(CounterplayKind.WHIFF));
        assertEquals(1, s.whiffHits);
        assertTrue(s.noteCounterplay(CounterplayKind.BREAK));
        assertEquals(1, s.breakHits);
    }

    @Test public void successFlash_D448_shortVerbs() {
        assertTrue(EmberCounterplay.successFlash(CounterplayKind.WALL).contains("撞墙破绽"));
        assertTrue(EmberCounterplay.successFlash(CounterplayKind.WHIFF).contains("落空破绽"));
        assertTrue(EmberCounterplay.successFlash(CounterplayKind.BREAK).contains("破招成功"));
        assertEquals("", EmberCounterplay.successFlash(null));
    }

    @Test public void shouldFlashSuccess_D448_debounce() {
        assertTrue(EmberCounterplay.shouldFlashSuccess(0L, 1000L));
        long t0 = 10_000L;
        assertFalse(EmberCounterplay.shouldFlashSuccess(t0, t0 + 799L));
        assertTrue(EmberCounterplay.shouldFlashSuccess(t0, t0 + 800L));
        assertEquals(800L, EmberCounterplay.SUCCESS_FLASH_DEBOUNCE_MS);
    }
}
