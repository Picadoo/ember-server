package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.encounter.BossMove;
import town.sunshine.corerpg.p1.encounter.CounterplayKind;
import town.sunshine.corerpg.p1.encounter.EmberBossMove;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * D239: pins BossMove adapter view + schedule helpers moved from EmberRunDirector
 * (same contracts EmberRunRulesTest already asserts via Director thin delegates).
 */
public class EmberBossMoveTest {

    private static EmberRunMaps.Skill skill(Object... kv) {
        Map<String, Object> m = new HashMap<String, Object>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return new EmberRunMaps.Skill(m);
    }

    @Test public void wrapsSkillFields() {
        EmberRunMaps.Skill sk = skill("type", "cone", "name", "重斩", "every", 8, "warn", 1.2, "below", 0.5, "whiff_stun", 0.5);
        BossMove m = EmberBossMove.of(sk);
        assertEquals("重斩", m.name());
        assertEquals("cone", m.shape());
        assertEquals(8.0, m.every(), 0);
        assertEquals(1.2, m.warn(), 1e-9);
        assertEquals(0.5, m.below(), 0);
        assertTrue(m.has(CounterplayKind.WHIFF));
        assertFalse(m.has(CounterplayKind.WALL));
        assertFalse(m.has(CounterplayKind.BREAK));
        assertSame(sk, m.skill());
        assertTrue(EmberBossMove.of(sk).gated());
    }

    @Test public void counterplayKindsOnChargeAndBreak() {
        EmberRunMaps.Skill charge = skill("type", "charge", "name", "冲撞", "length", 7, "width", 2, "wall_stun", 1.5);
        assertTrue(EmberBossMove.of(charge).has(CounterplayKind.WALL));
        assertFalse(EmberBossMove.of(charge).has(CounterplayKind.WHIFF)); // charge never carries whiff
        EmberRunMaps.Skill br = skill("type", "circle", "name", "聚爆", "radius", 4, "break_hp", 0.065, "break_stun", 0.5);
        assertTrue(EmberBossMove.of(br).has(CounterplayKind.BREAK));
        assertFalse(EmberBossMove.of(br).has(CounterplayKind.WALL));
    }

    @Test public void ofNullIsNull() {
        assertNull(EmberBossMove.of(null));
    }

    @Test public void dueSkillPicksFirstDue() {
        long[] next = {2000, 1000, 3000};
        assertEquals(1, EmberBossMove.dueSkill(next, 1500));
        assertEquals(-1, EmberBossMove.dueSkill(next, 999));
        assertEquals(0, EmberBossMove.dueSkill(new long[]{1000, 1000}, 1000)); // first-listed wins
    }

    @Test public void dueSkillRespectsBelowGate() {
        long[] next = {99999, 99999, 0};
        double[] below = {1.01, 1.01, 0.5};
        assertEquals(-1, EmberBossMove.dueSkill(next, 1000, below, 0.8)); // gated above 50 %
        assertEquals(2, EmberBossMove.dueSkill(next, 1000, below, 0.49));
    }

    @Test public void gatedNextFirstCastResetsFromNow() {
        assertEquals(5000L, EmberBossMove.gatedNext(0, 4000, 1000, true));
        assertEquals(5000L, EmberBossMove.gatedNext(1000, 4000, 1000, false)); // nextDue: 1000+4000
        assertEquals(Long.MAX_VALUE / 4, EmberBossMove.gatedNext(0, 0, 1000, false));
    }

    @Test public void nextDueAdvancesPastNow() {
        assertEquals(12L, EmberBossMove.nextDue(0, 4, 10));
        assertEquals(14L, EmberBossMove.nextDue(2, 4, 10));
    }

    @Test public void hasBelowPressureDetectsTopAndFollow() {
        EmberRunMaps.Boss b = new EmberRunMaps.Boss(Collections.<String, Object>emptyMap());
        // Boss ctor from empty map may not expose mutable skills; probe via Skill list helper path through hasBelowPressure on real boss from maps is covered by RulesTest.
        // Direct: build via reflection-free path — Skill with below, wrap as list on a Boss from yml-like map.
        Map<String, Object> sk = new HashMap<String, Object>();
        sk.put("type", "cone"); sk.put("name", "x"); sk.put("below", 0.5);
        Map<String, Object> bm = new HashMap<String, Object>();
        bm.put("mm", "Boss"); bm.put("name", "t"); bm.put("hp", 100); bm.put("atk", 1);
        bm.put("at", Arrays.asList(0, 64, 0));
        bm.put("skills", Collections.singletonList(sk));
        EmberRunMaps.Boss gated = new EmberRunMaps.Boss(bm);
        assertTrue(EmberBossMove.hasBelowPressure(gated));
        assertFalse(EmberBossMove.hasBelowPressure(null));
    }

    @Test public void belowOfCopiesSkillGates() {
        EmberRunMaps.Skill a = skill("type", "cone", "name", "a", "below", 1.01);
        EmberRunMaps.Skill b = skill("type", "cone", "name", "b", "below", 0.5);
        double[] out = EmberBossMove.belowOf(Arrays.asList(a, b));
        assertEquals(2, out.length);
        assertEquals(1.01, out[0], 1e-9);
        assertEquals(0.5, out[1], 0);
        assertEquals(0, EmberBossMove.belowOf(null).length);
    }
}
