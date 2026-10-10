package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberClaimPathTest {
    @Test public void holdAndBrief() {
        assertTrue(EmberClaimPath.shouldHoldAmbient(EmberClaimPath.HOLD));
        assertTrue(EmberClaimPath.shouldHoldSettle(EmberClaimPath.HOLD));
        assertFalse(EmberClaimPath.shouldHoldAmbient(EmberClaimPath.AUTO));
        assertFalse(EmberClaimPath.shouldHoldSettle(EmberClaimPath.BRIEF));
        assertTrue(EmberClaimPath.shouldBrief(EmberClaimPath.BRIEF));
        assertEquals(EmberClaimPath.HOLD, EmberClaimPath.parse("暂存"));
        assertEquals(EmberClaimPath.BRIEF, EmberClaimPath.parse("静默"));
    }
}
