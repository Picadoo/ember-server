package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberCreditPathTest {
    @Test public void policies() {
        assertTrue(EmberCreditPath.shouldUseCredit(EmberCreditPath.SPEND));
        assertTrue(EmberCreditPath.shouldUseCredit(EmberCreditPath.ASK));
        assertTrue(EmberCreditPath.shouldUseCredit(EmberCreditPath.NONE));
        assertFalse(EmberCreditPath.shouldUseCredit(EmberCreditPath.HOLD));
        assertEquals(EmberCreditPath.HOLD, EmberCreditPath.parse("保留"));
    }
}
