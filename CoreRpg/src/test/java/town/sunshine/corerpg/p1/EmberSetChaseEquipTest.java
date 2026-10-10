package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberSetChaseEquipTest {
    @Test public void findNullSafe() {
        assertNull(EmberSetChase.findEquipCandidate(null, null, null, null));
    }
    @Test public void focusGapEmpty() {
        assertEquals("", EmberSetChase.focusGap(null, null));
    }
}
