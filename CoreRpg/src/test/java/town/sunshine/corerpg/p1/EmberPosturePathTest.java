package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberPosturePathTest {
    @Test public void parse_map_shape_dir() {
        assertEquals(EmberPosturePath.STRIKE, EmberPosturePath.parse("突进"));
        assertEquals(EmberPosturePath.GUARD, EmberPosturePath.parse("guard"));
        assertEquals(EmberPosturePath.SWEEP, EmberPosturePath.parse("清杂"));
        assertEquals(EmberSkillKit.SHAPE_LINE, EmberPosturePath.shapeId(EmberPosturePath.STRIKE));
        assertEquals(EmberSkillKit.DIR_BACK, EmberPosturePath.dirId(EmberPosturePath.GUARD));
        assertEquals(EmberSkillKit.SHAPE_FAN, EmberPosturePath.shapeId(EmberPosturePath.SWEEP));
        assertEquals(EmberShapePath.RING, EmberPosturePath.shapePathId(EmberPosturePath.GUARD));
        assertEquals(EmberStepPath.FORWARD, EmberPosturePath.stepPathId(EmberPosturePath.STRIKE));
        assertTrue(EmberPosturePath.tip(EmberPosturePath.SWEEP).contains("扇"));
    }
}
