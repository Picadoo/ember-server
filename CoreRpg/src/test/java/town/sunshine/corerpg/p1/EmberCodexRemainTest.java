package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

public class EmberCodexRemainTest {
    @Test public void progressRemain_empty() {
        // null-safe count path: claimable/count tolerate null → NPE on count; use fake via no pd
        // progressRemain(null) uses count(null)=0
        try {
            String s = EmberCodex.progressRemain(null);
            assertTrue(s.contains("图录"));
            assertTrue(s.contains("还差") || s.contains("可领") || s.contains("全领"));
        } catch (NullPointerException npe) {
            // if count(null) NPEs, document — prefer fix count
            fail("progressRemain should tolerate null pd: " + npe);
        }
    }
}
