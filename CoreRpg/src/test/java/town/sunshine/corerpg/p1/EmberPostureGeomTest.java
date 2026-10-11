package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberPostureGeomTest {
    @Test public void lineWiderWhenPosture() {
        EmberSkillKit.Shape a = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 3.5, 100.0, 5, false, false, null, false);
        EmberSkillKit.Shape b = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_LINE, 3.5, 100.0, 5, false, false, null, true);
        assertTrue(b.range > a.range);
        assertTrue(b.lineWidth > a.lineWidth);
    }
    @Test public void fanArcWiderWhenPosture() {
        EmberSkillKit.Shape a = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100.0, 5, false, false, null, false);
        EmberSkillKit.Shape b = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_FAN, 3.5, 100.0, 5, false, false, null, true);
        assertTrue(b.arc > a.arc);
    }
    @Test public void ringRangeWiderWhenPosture() {
        EmberSkillKit.Shape a = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_RING, 3.5, 100.0, 5, false, false, null, false);
        EmberSkillKit.Shape b = EmberSkillKit.geometryFor(EmberSkillKit.SHAPE_RING, 3.5, 100.0, 5, false, false, null, true);
        assertTrue(b.range > a.range);
    }
}
