package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberStepPathTest {
    @Test public void parse_map_dir() {
        assertEquals(EmberStepPath.FORWARD, EmberStepPath.parse("前冲"));
        assertEquals(EmberStepPath.BACK, EmberStepPath.parse("back"));
        assertEquals(EmberStepPath.NONE, EmberStepPath.parse("clear"));
        assertEquals(EmberSkillKit.DIR_BACK, EmberStepPath.toDirId(EmberStepPath.BACK));
        assertEquals(EmberSkillKit.DIR_FORWARD, EmberStepPath.toDirId(EmberStepPath.FORWARD));
        assertTrue(EmberStepPath.tip(EmberStepPath.BACK).contains("后"));
        assertTrue(EmberStepPath.enterCmd(EmberStepPath.FORWARD).contains("q05"));
    }
}
