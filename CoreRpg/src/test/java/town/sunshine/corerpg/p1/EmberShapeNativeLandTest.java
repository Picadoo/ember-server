package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberShapeNativeLandTest {
    @Test
    public void lineAddsSlow() {
        int fan = EmberSkillKit.slashLandSlowTicks(false, false, false, EmberSkillKit.SHAPE_FAN);
        int line = EmberSkillKit.slashLandSlowTicks(false, false, false, EmberSkillKit.SHAPE_LINE);
        assertEquals(fan + 2, line);
    }
    @Test
    public void ringAddsHeal() {
        double fan = EmberSkillKit.slashLandHealPct(false, false, false, EmberSkillKit.SHAPE_FAN);
        double ring = EmberSkillKit.slashLandHealPct(false, false, false, EmberSkillKit.SHAPE_RING);
        assertEquals(fan * 1.04, ring, 1e-9);
    }
    @Test
    public void ringDashWider() {
        double fan = EmberSkillKit.dashHitRadius(false, false, EmberSkillKit.SHAPE_FAN);
        double ring = EmberSkillKit.dashHitRadius(false, false, EmberSkillKit.SHAPE_RING);
        assertEquals(fan + 0.10, ring, 1e-9);
    }
}
