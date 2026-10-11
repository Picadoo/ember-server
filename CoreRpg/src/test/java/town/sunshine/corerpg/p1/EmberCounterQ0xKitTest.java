package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberCounterQ0xKitTest {
    @Test
    public void q03ParryWider() {
        long base = EmberSkillKit.parryWindowMs(false, false, null);
        long q03 = EmberSkillKit.parryWindowMs(false, false, "q03");
        assertEquals(base + 30L, q03);
    }

    @Test
    public void q02DashWider() {
        double base = EmberSkillKit.dashHitRadius(false, false, EmberSkillKit.SHAPE_FAN, null);
        double q02 = EmberSkillKit.dashHitRadius(false, false, EmberSkillKit.SHAPE_FAN, "q02");
        assertEquals(base + 0.08, q02, 1e-9);
    }

    @Test
    public void q05StepLonger() {
        double base = EmberSkillKit.stepTravelDistance(3.0, false, false, 0, null);
        double q05 = EmberSkillKit.stepTravelDistance(3.0, false, false, 0, "q05");
        assertEquals(base + 0.35, q05, 1e-9);
    }

    @Test
    public void q04GeoWider() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, null);
        EmberSkillKit.Shape q04 = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q04");
        assertEquals(base.arc + 4.0, q04.arc, 1e-9);
    }

    @Test
    public void otherMapsNoBonus() {
        assertEquals(
            EmberSkillKit.parryWindowMs(false, false, null),
            EmberSkillKit.parryWindowMs(false, false, "q01"));
    }
}
