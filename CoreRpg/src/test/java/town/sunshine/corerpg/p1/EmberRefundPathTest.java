package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberRefundPathTest {
    @Test public void effectiveMax_never_raises() {
        assertEquals(5, EmberRefundPath.effectiveMax(EmberRefundPath.NONE, 5));
        assertEquals(5, EmberRefundPath.effectiveMax(EmberRefundPath.FULL, 5));
        assertEquals(2, EmberRefundPath.effectiveMax(EmberRefundPath.LIGHT, 5));
        assertEquals(2, EmberRefundPath.effectiveMax(EmberRefundPath.LIGHT, 2));
        assertEquals(1, EmberRefundPath.effectiveMax(EmberRefundPath.LIGHT, 1));
        assertEquals(0, EmberRefundPath.effectiveMax(EmberRefundPath.BARE, 5));
        assertEquals(0, EmberRefundPath.effectiveMax(EmberRefundPath.FULL, 0));
    }
    @Test public void parse() {
        assertEquals(EmberRefundPath.FULL, EmberRefundPath.parse("满退"));
        assertEquals(EmberRefundPath.LIGHT, EmberRefundPath.parse("轻退"));
        assertEquals(EmberRefundPath.BARE, EmberRefundPath.parse("不退"));
    }
}
