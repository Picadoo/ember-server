package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberQ01Q06Q07KitTest {
    @Test
    public void q01FanMoreTargets() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, null);
        EmberSkillKit.Shape q01 = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100, 5, false, false, "q01");
        assertEquals(base.maxTargets + 1, q01.maxTargets);
    }

    @Test
    public void q01LineLonger() {
        EmberSkillKit.Shape base = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 3.5, 100, 5, false, false, null);
        EmberSkillKit.Shape q01 = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 3.5, 100, 5, false, false, "q01");
        assertEquals(base.range + 0.15, q01.range, 1e-9);
    }

    @Test
    public void q06StepHeal() {
        double base = EmberSkillKit.stepLandHealPct(false, false, null);
        double q06 = EmberSkillKit.stepLandHealPct(false, false, "q06");
        assertEquals(base * 1.08, q06, 1e-9);
    }

    @Test
    public void q07ChargeFaster() {
        double base = EmberSkillKit.sigChargeSecs(2.0, false, null);
        double q07 = EmberSkillKit.sigChargeSecs(2.0, false, "q07");
        assertEquals(base * 0.92, q07, 1e-9);
    }

    @Test
    public void otherMapsNoBonus() {
        assertEquals(
            EmberSkillKit.stepLandHealPct(false, false, null),
            EmberSkillKit.stepLandHealPct(false, false, "q01"), 1e-9);
        assertEquals(
            EmberSkillKit.sigChargeSecs(2.0, false, null),
            EmberSkillKit.sigChargeSecs(2.0, false, "q01"), 1e-9);
    }
}
