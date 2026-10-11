package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberImprintBreakTest {
    @Test
    public void autoKeyPrefix() {
        assertEquals("p1_imprint_auto_", EmberImprintBreak.C_AUTO);
    }

    @Test
    public void townOkFalseWithoutRuns() {
        assertFalse(EmberImprintBreak.townOk(null, null));
    }

    @Test
    public void fitsEmptyBladeForL01() {
        EmberSignature.Def def = EmberSignature.byId("L01");
        assertNotNull(def);
        EmberItemData blade = EmberItemData.create(def.family, "blade", 1, 1, 0, 0, true, "drop");
        assertTrue(EmberSignature.fits(def, blade.slot, blade.family));
    }
}
