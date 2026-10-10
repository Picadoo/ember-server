package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberBreakPathTest {
    @Test public void effectiveBreak_never_raises() {
        assertEquals(10.0, EmberBreakPath.effectiveBreak(EmberBreakPath.NONE, 10.0), 1e-9);
        assertEquals(10.0, EmberBreakPath.effectiveBreak(EmberBreakPath.FULL, 10.0), 1e-9);
        assertEquals(5.0, EmberBreakPath.effectiveBreak(EmberBreakPath.SHORT, 10.0), 1e-9);
        assertEquals(2.5, EmberBreakPath.effectiveBreak(EmberBreakPath.SNAP, 10.0), 1e-9);
        assertEquals(2.0, EmberBreakPath.effectiveBreak(EmberBreakPath.SNAP, 4.0), 1e-9); // floor
        assertEquals(0.0, EmberBreakPath.effectiveBreak(EmberBreakPath.FULL, 0.0), 1e-9);
    }
    @Test public void parse() {
        assertEquals(EmberBreakPath.FULL, EmberBreakPath.parse("满歇"));
        assertEquals(EmberBreakPath.SHORT, EmberBreakPath.parse("短歇"));
        assertEquals(EmberBreakPath.SNAP, EmberBreakPath.parse("急歇"));
    }
}
