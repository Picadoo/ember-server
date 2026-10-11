package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberPostureGeomEdgesTest {
    @Test public void dashWiderWhenPosture() {
        double a = EmberSkillKit.dashHitRadius(false, false, EmberSkillKit.SHAPE_FAN, null, false);
        double b = EmberSkillKit.dashHitRadius(false, false, EmberSkillKit.SHAPE_FAN, null, true);
        assertEquals(0.12, b - a, 1e-9);
    }
    @Test public void stepIgniteWider() {
        assertTrue(EmberSkillKit.stepIgniteRadius(true) > EmberSkillKit.stepIgniteRadius(false));
    }
    @Test public void parryNonGuardAligned() {
        long base = EmberSkillKit.parryWindowMs(false, false, null, false);
        long strike = EmberSkillKit.parryWindowMs(false, false, null, true);
        long guard = EmberSkillKit.parryWindowMs(false, true, null, true);
        assertEquals(25L, strike - base);
        assertEquals(50L, guard - base);
    }
    @Test public void stepTravelSyncBonus() {
        double a = EmberSkillKit.stepTravelDistance(5.0, false, true, EmberPosturePath.STRIKE, null, false);
        double b = EmberSkillKit.stepTravelDistance(5.0, false, true, EmberPosturePath.STRIKE, null, true);
        assertEquals(0.2, b - a, 1e-9);
    }
}
