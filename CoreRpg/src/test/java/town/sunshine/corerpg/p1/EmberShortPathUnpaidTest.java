package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberShortPathUnpaidTest {
    @Test public void unpaidLineNullSafe() {
        assertTrue(EmberShortPath.unpaidFcLine(null, null).contains("已清"));
        assertEquals(0, EmberShortPath.countUnpaidFc(null, null));
    }
}
