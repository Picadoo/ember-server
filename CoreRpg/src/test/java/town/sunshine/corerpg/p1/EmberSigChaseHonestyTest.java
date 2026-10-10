package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberSigChaseHonestyTest {
    @Test public void remainLineReadyAndGap() {
        EmberSignature.Def def = EmberSignature.DEFS.get(0);
        assertNotNull(def);
        int need = EmberSignature.imprintMarks();
        assertTrue(EmberSigChase.remainLine(def, need).contains("可烙"));
        assertTrue(EmberSigChase.remainLine(def, 0).contains("还差"));
        assertTrue(EmberSigChase.remainLine(null, 0).contains("未选"));
    }

    @Test public void statusGlanceEmptyWithoutData() {
        assertEquals("", EmberSigChase.statusGlance(null));
    }
}
