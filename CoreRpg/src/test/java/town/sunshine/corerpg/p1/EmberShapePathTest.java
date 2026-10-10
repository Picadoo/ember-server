package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberShapePathTest {
    @Test public void parse_map_shape() {
        assertEquals(EmberShapePath.FAN, EmberShapePath.parse("扇形"));
        assertEquals(EmberShapePath.LINE, EmberShapePath.parse("line"));
        assertEquals(EmberShapePath.RING, EmberShapePath.parse("环斩"));
        assertEquals(EmberShapePath.NONE, EmberShapePath.parse("clear"));
        assertEquals(EmberSkillKit.SHAPE_LINE, EmberShapePath.toShapeId(EmberShapePath.LINE));
        assertEquals(EmberSkillKit.SHAPE_RING, EmberShapePath.toShapeId(EmberShapePath.RING));
        assertEquals(EmberSkillKit.SHAPE_FAN, EmberShapePath.toShapeId(EmberShapePath.FAN));
        assertTrue(EmberShapePath.tip(EmberShapePath.RING).contains("环"));
        assertTrue(EmberShapePath.enterCmd(EmberShapePath.LINE).contains("q02"));
    }
}
