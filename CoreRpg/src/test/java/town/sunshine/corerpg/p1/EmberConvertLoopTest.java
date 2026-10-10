package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberConvertLoopTest {
    @Test public void path_hit() {
        assertTrue(EmberConvertLoop.pathHit(EmberConvertPath.SCORCH, "scorch"));
        assertFalse(EmberConvertLoop.pathHit(EmberConvertPath.SCORCH, "burst"));
        assertFalse(EmberConvertLoop.pathHit(EmberConvertPath.NONE, "scorch"));
    }
}
