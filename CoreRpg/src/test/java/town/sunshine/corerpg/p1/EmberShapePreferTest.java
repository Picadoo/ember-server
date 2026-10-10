package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberShapePreferTest {
    @Test public void preferred_by_set() {
        assertEquals(EmberSkillKit.SHAPE_FAN, EmberSkillKit.preferredShape("scorch"));
        assertEquals(EmberSkillKit.SHAPE_LINE, EmberSkillKit.preferredShape("burst"));
        assertEquals(EmberSkillKit.SHAPE_RING, EmberSkillKit.preferredShape("sustain"));
        assertEquals("扇形", EmberSkillKit.preferredShapeLabel("scorch"));
        assertEquals("直线穿刺", EmberSkillKit.preferredShapeLabel("burst"));
        assertEquals("原地环斩", EmberSkillKit.preferredShapeLabel("sustain"));
    }
}
