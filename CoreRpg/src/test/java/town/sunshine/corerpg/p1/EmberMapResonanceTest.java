package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmberMapResonanceTest {

    @Test
    public void baseMap() {
        assertEquals("q01", EmberMapResonance.baseMap("q01"));
        assertEquals("q01", EmberMapResonance.baseMap("q01c"));
        assertEquals("q07", EmberMapResonance.baseMap("q07a2"));
        assertEquals("", EmberMapResonance.baseMap(null));
    }

    @Test
    public void matchOrigin() {
        EmberItemData.Origin o = EmberItemData.Origin.of("q04", "S01", "run1", 1L);
        assertTrue(EmberMapResonance.matches(o, "q04"));
        assertTrue(EmberMapResonance.matches(o, "q04c"));
        assertFalse(EmberMapResonance.matches(o, "q05"));
        assertFalse(EmberMapResonance.matches(EmberItemData.Origin.NONE, "q04"));
    }

    @Test
    public void geomResonanceWidensFan() {
        EmberSkillKit.Shape a = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q02", false, false);
        EmberSkillKit.Shape b = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q02", false, true);
        assertTrue(b.arc > a.arc);
    }

    @Test
    public void shouldWearCue_risingEdge() {
        assertTrue(EmberMapResonance.shouldWearCue(false, true, false, true));
    }

    @Test
    public void shouldWearCue_trueTrueQuiet() {
        assertFalse(EmberMapResonance.shouldWearCue(true, true, false, true));
    }

    @Test
    public void shouldWearCue_afterLatchQuietEvenRising() {
        assertFalse(EmberMapResonance.shouldWearCue(false, true, true, true));
    }

    @Test
    public void shouldWearCue_disabled() {
        assertFalse(EmberMapResonance.shouldWearCue(false, true, false, false));
    }

    @Test
    public void shouldWearCue_fallingOrIdle() {
        assertFalse(EmberMapResonance.shouldWearCue(true, false, false, true));
        assertFalse(EmberMapResonance.shouldWearCue(false, false, false, true));
    }

    @Test
    public void nextSnapshot_isNow() {
        assertTrue(EmberMapResonance.nextSnapshot(false, true));
        assertFalse(EmberMapResonance.nextSnapshot(true, false));
        assertTrue(EmberMapResonance.nextSnapshot(true, true));
    }

    @Test
    public void wearCueEnabled_defaultTrue_and_testHook() {
        EmberMapResonance.setWearCueEnabledForTest(null);
        assertTrue(EmberMapResonance.wearCueEnabled());
        EmberMapResonance.setWearCueEnabledForTest(Boolean.FALSE);
        assertFalse(EmberMapResonance.wearCueEnabled());
        assertFalse(EmberMapResonance.shouldWearCue(false, true, false, EmberMapResonance.wearCueEnabled()));
        EmberMapResonance.setWearCueEnabledForTest(null);
        assertTrue(EmberMapResonance.wearCueEnabled());
        assertTrue(EmberMapResonance.shouldWearCue(false, true, false, EmberMapResonance.wearCueEnabled()));
    }

    @Test
    public void widenAndWearBoneLiteralsUnchanged() {
        // D12432: prove SkillKit / DayBoost / WearBone amounts not lifted by this tip
        assertEquals(0.08, EmberMapResonanceDayBoost.EXTRA_RANGE, 0.0);
        assertEquals(0.10, EmberMapResonanceDayBoost.EXTRA_LINE, 0.0);
        assertEquals(0.03, EmberMapResonanceDayBoost.EXTRA_LINE_W, 0.0);
        assertEquals(1, EmberMapOriginWearBone.BONUS);
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q02", false, false);
        EmberSkillKit.Shape reso = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q02", false, true);
        assertEquals(6.0, reso.arc - base.arc, 0.001);
        EmberSkillKit.Shape line0 = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 4.0, 0, 5, false, false, "q02", false, false);
        EmberSkillKit.Shape line1 = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 4.0, 0, 5, false, false, "q02", false, true);
        assertEquals(0.2, line1.range - line0.range, 0.001);
    }
}
