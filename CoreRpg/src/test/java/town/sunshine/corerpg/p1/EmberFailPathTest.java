package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberFailPathTest {
    @Test public void effectiveShare_never_raises() {
        assertEquals(0.5, EmberFailPath.effectiveShare(EmberFailPath.NONE, 0.5), 1e-9);
        assertEquals(0.5, EmberFailPath.effectiveShare(EmberFailPath.KEEP, 0.5), 1e-9);
        assertEquals(0.25, EmberFailPath.effectiveShare(EmberFailPath.LIGHT, 0.5), 1e-9);
        assertEquals(0.0, EmberFailPath.effectiveShare(EmberFailPath.SKIP, 0.5), 1e-9);
        assertEquals(0.0, EmberFailPath.effectiveShare(EmberFailPath.KEEP, 0.0), 1e-9);
        assertEquals(15, EmberRunRules.failRefundAmount(30, EmberFailPath.effectiveShare(EmberFailPath.KEEP, 0.5)));
        assertEquals(7, EmberRunRules.failRefundAmount(30, EmberFailPath.effectiveShare(EmberFailPath.LIGHT, 0.5)));
        assertEquals(0, EmberRunRules.failRefundAmount(30, EmberFailPath.effectiveShare(EmberFailPath.SKIP, 0.5)));
    }
    @Test public void parse() {
        assertEquals(EmberFailPath.KEEP, EmberFailPath.parse("满退"));
        assertEquals(EmberFailPath.LIGHT, EmberFailPath.parse("轻退"));
        assertEquals(EmberFailPath.SKIP, EmberFailPath.parse("不退"));
    }
}
