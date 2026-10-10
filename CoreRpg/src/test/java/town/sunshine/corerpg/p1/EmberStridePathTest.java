package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberStridePathTest {
    @Test public void parse_map_flex_step() {
        assertEquals(EmberStridePath.PUSH, EmberStridePath.parse("突进"));
        assertEquals(EmberStridePath.BAIL, EmberStridePath.parse("bail"));
        assertEquals(EmberStridePath.BARE, EmberStridePath.parse("卸下"));
        assertEquals(EmberFlexPath.ON, EmberStridePath.flexId(EmberStridePath.PUSH));
        assertEquals(EmberStepPath.FORWARD, EmberStridePath.stepId(EmberStridePath.PUSH));
        assertEquals(EmberStepPath.BACK, EmberStridePath.stepId(EmberStridePath.BAIL));
        assertEquals(EmberFlexPath.OFF, EmberStridePath.flexId(EmberStridePath.BARE));
        assertTrue(EmberStridePath.tip(EmberStridePath.BAIL).contains("后撤"));
    }
}
