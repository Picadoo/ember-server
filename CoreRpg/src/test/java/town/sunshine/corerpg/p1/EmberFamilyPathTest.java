package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFamilyPathTest {
    @Test public void parse_tips_step() {
        assertEquals(EmberFamilyPath.SCORCH, EmberFamilyPath.parse("火痕"));
        assertEquals(EmberFamilyPath.BURST, EmberFamilyPath.parse("burst"));
        assertEquals(EmberFamilyPath.SUSTAIN, EmberFamilyPath.parse("承护"));
        assertEquals(EmberFamilyPath.NONE, EmberFamilyPath.parse("clear"));
        assertEquals("scorch", EmberFamilyPath.familyKey(EmberFamilyPath.SCORCH));
        assertTrue(EmberFamilyPath.tip(EmberFamilyPath.BURST).contains("爆闪"));
        assertTrue(EmberFamilyPath.stepName(EmberFamilyPath.SCORCH).contains("火痕"));
    }
}
