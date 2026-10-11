package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberExchangeNeedOverrideTest {
    @Test public void softNeedPasses() {
        assertNull(EmberRunRules.exchangeCheck(4, 1, 1, "scorch", "blade", 4));
        assertNotNull(EmberRunRules.exchangeCheck(4, 1, 1, "scorch", "blade", 8));
    }
}
