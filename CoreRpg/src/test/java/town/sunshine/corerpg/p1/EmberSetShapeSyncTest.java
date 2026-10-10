package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

/** D2253/D2266: set-synced shape geometry + land (zero skill_mult). */
public class EmberSetShapeSyncTest {

    @Test
    public void syncedDetectsPreferred() {
        assertTrue(EmberSkillKit.setShapeSynced("burst", EmberSkillKit.SHAPE_LINE));
        assertTrue(EmberSkillKit.setShapeSynced("sustain", EmberSkillKit.SHAPE_RING));
        assertTrue(EmberSkillKit.setShapeSynced("scorch", EmberSkillKit.SHAPE_FAN));
        assertFalse(EmberSkillKit.setShapeSynced("burst", EmberSkillKit.SHAPE_FAN));
    }

    @Test
    public void lineWiderWhenSynced() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 3.5, 100, 5, false);
        EmberSkillKit.Shape sync = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 3.5, 100, 5, true);
        assertEquals(0.7, base.lineWidth, 1e-6);
        assertEquals(0.95, sync.lineWidth, 1e-6);
        assertEquals(3, base.maxTargets);
        assertEquals(4, sync.maxTargets);
    }

    @Test
    public void ringBiggerWhenSynced() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_RING, 3.5, 100, 5, false);
        EmberSkillKit.Shape sync = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_RING, 3.5, 100, 5, true);
        assertEquals(3.5, base.range, 1e-6);
        assertEquals(3.9, sync.range, 1e-6);
    }

    @Test
    public void fanWiderArcWhenSynced() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false);
        EmberSkillKit.Shape sync = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, true);
        assertEquals(110.0, base.arc, 1e-6);
        assertEquals(122.0, sync.arc, 1e-6);
    }

    @Test
    public void landSlowAndHealBumpWhenSynced() {
        assertEquals(20, EmberSkillKit.slashLandSlowTicks(false));
        assertEquals(24, EmberSkillKit.slashLandSlowTicks(true));
        assertEquals(0.002, EmberSkillKit.slashLandHealPct(false), 1e-9);
        assertEquals(0.0025, EmberSkillKit.slashLandHealPct(true), 1e-9);
    }
}
