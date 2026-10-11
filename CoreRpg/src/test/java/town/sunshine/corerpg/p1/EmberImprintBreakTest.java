package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberImprintBreakTest {
    @Test
    public void autoKeyPrefix() {
        assertEquals("p1_imprint_auto_", EmberImprintBreak.C_AUTO);
    }

    @Test
    public void canAutoFalseWithoutRuns() {
        assertFalse(EmberImprintBreak.canAuto(null, null, EmberSignature.byId("L01"), null));
    }
}
