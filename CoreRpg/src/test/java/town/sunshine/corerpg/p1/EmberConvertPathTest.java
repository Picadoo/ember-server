package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberConvertPathTest {
    @Test public void parse_glance() {
        assertEquals(EmberConvertPath.SCORCH, EmberConvertPath.parse("焚烬"));
        assertEquals(EmberConvertPath.BURST, EmberConvertPath.parse("burst"));
        assertEquals(EmberConvertPath.NONE, EmberConvertPath.parse("clear"));
        assertEquals("scorch", EmberConvertPath.familyKey(EmberConvertPath.SCORCH));
        assertTrue(EmberConvertPath.glance(EmberConvertPath.SCORCH, "scorch", "scorch").contains("同族"));
    }
}
